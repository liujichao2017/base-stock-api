package com.hm.stock.domain.borrow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.auth.entity.SessionInfo;
import com.hm.stock.domain.borrow.entity.BorrowCoupons;
import com.hm.stock.domain.borrow.entity.MemberBorrowRecord;
import com.hm.stock.domain.borrow.mapper.BorrowCouponsMapper;
import com.hm.stock.domain.borrow.mapper.MemberBorrowRecordMapper;
import com.hm.stock.domain.borrow.service.BorrowCouponsService;
import com.hm.stock.domain.borrow.vo.BorrowBuyVo;
import com.hm.stock.domain.member.entity.Member;
import com.hm.stock.domain.member.enums.FundsOperateTypeEnum;
import com.hm.stock.domain.member.enums.FundsSourceEnum;
import com.hm.stock.domain.member.service.MemberFundsService;
import com.hm.stock.domain.member.vo.FundsOperateVo;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.enums.YNEnum;
import com.hm.stock.modules.execptions.ErrorResultCode;
import com.hm.stock.modules.utils.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class BorrowCouponsServiceImpl extends ServiceImpl<BorrowCouponsMapper, BorrowCoupons> implements BorrowCouponsService {
    @Autowired
    private MemberFundsService memberFundsService;
    @Autowired
    private MemberBorrowRecordMapper memberBorrowRecordMapper;

    @Override
    public List<BorrowCoupons> selectList(BorrowCoupons query) {
        QueryWrapper<BorrowCoupons> ew = new QueryWrapper<>();
        query.setStatus("1");
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    public PageDate<BorrowCoupons> selectByPage(BorrowCoupons query, PageParam page) {
        QueryWrapper<BorrowCoupons> ew = new QueryWrapper<>();
        query.setStatus("1");
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<BorrowCoupons> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public BorrowCoupons detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(BorrowCoupons body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(BorrowCoupons body) {
        return updateById(body);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean buy(BorrowBuyVo body) {
        BorrowCoupons borrowCoupons = getById(body.getId());
        LogicUtils.assertNotNull(borrowCoupons, ErrorResultCode.E000028);
        LogicUtils.assertTrue(YNEnum.yes(borrowCoupons.getStatus()), ErrorResultCode.E000038);

        long stockNum = body.getNum() * borrowCoupons.getNum();
        BigDecimal buyMoney = BigDecimal.valueOf(stockNum).multiply(borrowCoupons.getPrice());
        LogicUtils.assertTrue(borrowCoupons.getMinBuyAmt().compareTo(buyMoney) <= 0, ErrorResultCode.E000032);

        Member member = SessionInfo.getMember();

        MemberBorrowRecord memberBorrowRecord = new MemberBorrowRecord();
        memberBorrowRecord.setMemberId(member.getId());
        memberBorrowRecord.setBcId(borrowCoupons.getId());
        memberBorrowRecord.setStockName(borrowCoupons.getStockName());
        memberBorrowRecord.setStockCode(borrowCoupons.getStockCode());
        memberBorrowRecord.setNum(borrowCoupons.getNum());
        memberBorrowRecord.setBuyNum(body.getNum());
        memberBorrowRecord.setApplyNum(0L);
        memberBorrowRecord.setBuyPrice(borrowCoupons.getPrice());
        memberBorrowRecord.setBuyAmt(buyMoney);
        memberBorrowRecord.setBuyTime(new Date());
        memberBorrowRecord.setCycle(borrowCoupons.getCycle());

        memberBorrowRecord.setRebate(borrowCoupons.getRebate());


        BigDecimal rebate = memberBorrowRecord.getRebate();
        rebate = rebate.divide(BigDecimal.valueOf(100), 4, 4);
        BigDecimal totalIncome = memberBorrowRecord.getBuyAmt().multiply(rebate);
        BigDecimal dayIncome = totalIncome.divide(BigDecimal.valueOf(memberBorrowRecord.getCycle()), 2, 4);
        memberBorrowRecord.setTotalIncome(totalIncome);
        memberBorrowRecord.setDayIncome(dayIncome);
        memberBorrowRecord.setCycleTime(DateTimeUtil.getDate(borrowCoupons.getCycle()));

        memberBorrowRecord.setIncome(new BigDecimal("0"));
        memberBorrowRecord.setMarketId(borrowCoupons.getMarketId());
        memberBorrowRecord.setStatus(1);
        LogicUtils.assertTrue(memberBorrowRecordMapper.insert(memberBorrowRecord) == 1, ErrorResultCode.E000001);


        FundsOperateVo fundsOperateVo = new FundsOperateVo();
        fundsOperateVo.setSource(FundsSourceEnum.BORROW);
        fundsOperateVo.setOperateType(FundsOperateTypeEnum.FREEZE);
        fundsOperateVo.setMember(member);
        fundsOperateVo.setMarketId(borrowCoupons.getMarketId());
        fundsOperateVo.setAmt(buyMoney);
        fundsOperateVo.setSourceId(memberBorrowRecord.getId());
        fundsOperateVo.setName(borrowCoupons.getStockName());
        fundsOperateVo.setCode(borrowCoupons.getStockCode());
        fundsOperateVo.set(FundsOperateVo.FundsInfoKey.STOCK_NAME, borrowCoupons.getStockName())
                .set(FundsOperateVo.FundsInfoKey.STOCK_CODE, borrowCoupons.getStockCode())
                .set(FundsOperateVo.FundsInfoKey.FREEZE_AMT, buyMoney)
                .build();
        memberFundsService.addFreezeAmt(fundsOperateVo);

        return true;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean compute() {
//        if (DateTimeUtil.isWeekend()) {
//            return false;
//        }

        QueryWrapper<MemberBorrowRecord> ew = new QueryWrapper<>();
        ew.eq("status", "2");
        ew.isNull("sell_time");
        List<MemberBorrowRecord> list = memberBorrowRecordMapper.selectList(ew);
        if (LogicUtils.isEmpty(list)) {
            return false;
        }

        for (MemberBorrowRecord memberBorrowRecord : list) {
            BigDecimal dayIncome = memberBorrowRecord.getDayIncome();
            BigDecimal buyAmt = BigDecimal.ZERO;
            if (DateTimeUtil.isExpire(memberBorrowRecord.getCycleTime())) {
                dayIncome = memberBorrowRecord.getTotalIncome().subtract(memberBorrowRecord.getIncome());
                buyAmt = memberBorrowRecord.getBuyAmt();
                memberBorrowRecord.setStatus(4);
                memberBorrowRecord.setBuyTime(new Date());
            }
            BigDecimal income = memberBorrowRecord.getIncome();
            income = income.add(dayIncome);
            memberBorrowRecord.setIncome(income);


            LogicUtils.assertEquals(memberBorrowRecordMapper.updateById(memberBorrowRecord), 1,
                                    ErrorResultCode.E000001);
            YNEnum visible = YNEnum.YES;
            if (buyAmt.compareTo(BigDecimal.ZERO) > 0) {
                visible = YNEnum.NO;
                FundsOperateVo fundsOperateVo2 = new FundsOperateVo();
                fundsOperateVo2.setSource(FundsSourceEnum.BORROW);
                fundsOperateVo2.setOperateType(FundsOperateTypeEnum.SELL);
                fundsOperateVo2.setMemberId(memberBorrowRecord.getMemberId());
                fundsOperateVo2.setMarketId(memberBorrowRecord.getMarketId());
                fundsOperateVo2.setAmt(buyAmt);
                fundsOperateVo2.setSourceId(memberBorrowRecord.getId());
                fundsOperateVo2.setName(memberBorrowRecord.getStockName());
                fundsOperateVo2.setCode(memberBorrowRecord.getStockCode());
                fundsOperateVo2.set(FundsOperateVo.FundsInfoKey.STOCK_NAME, memberBorrowRecord.getStockName())
                        .set(FundsOperateVo.FundsInfoKey.STOCK_CODE, memberBorrowRecord.getStockCode())
                        .set(FundsOperateVo.FundsInfoKey.OCCUPANCY_AMT, buyAmt)
                        .set(FundsOperateVo.FundsInfoKey.PROFIT_LOSS_AMT, memberBorrowRecord.getIncome())
                        .build();
                memberFundsService.subOccupancyAmt(fundsOperateVo2);
            }

            FundsOperateVo fundsOperatVo1 = new FundsOperateVo();
            fundsOperatVo1.setSource(FundsSourceEnum.BORROW);
            fundsOperatVo1.setOperateType(FundsOperateTypeEnum.PROFIT_LOSS);
            fundsOperatVo1.setMemberId(memberBorrowRecord.getMemberId());
            fundsOperatVo1.setMarketId(memberBorrowRecord.getMarketId());
            fundsOperatVo1.setAmt(dayIncome);
            fundsOperatVo1.setSourceId(memberBorrowRecord.getId());
            fundsOperatVo1.setName(memberBorrowRecord.getStockName());
            fundsOperatVo1.setCode(memberBorrowRecord.getStockCode());
            fundsOperatVo1.setVisible(visible.getType());
            fundsOperatVo1.set(FundsOperateVo.FundsInfoKey.STOCK_NAME, memberBorrowRecord.getStockName())
                    .set(FundsOperateVo.FundsInfoKey.STOCK_CODE, memberBorrowRecord.getStockCode())
                    .set(FundsOperateVo.FundsInfoKey.PROFIT_LOSS_AMT, dayIncome)
                    .build();
            memberFundsService.addProfitAmt(fundsOperatVo1);
        }
        return true;
    }
}
