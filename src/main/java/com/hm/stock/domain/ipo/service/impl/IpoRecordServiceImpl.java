package com.hm.stock.domain.ipo.service.impl;
import java.math.BigDecimal;

import com.hm.stock.domain.market.mapper.MarketMapper;
import com.hm.stock.domain.member.entity.MemberFunds;
import com.hm.stock.domain.member.enums.FundsSourceEnum;
import com.hm.stock.domain.market.entity.Market;
import com.hm.stock.domain.member.enums.FundsOperateTypeEnum;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.auth.entity.SessionInfo;
import com.hm.stock.domain.ipo.entity.IpoRecord;
import com.hm.stock.domain.ipo.mapper.IpoRecordMapper;
import com.hm.stock.domain.ipo.service.IpoRecordService;
import com.hm.stock.domain.member.entity.Member;
import com.hm.stock.domain.member.service.MemberFundsService;
import com.hm.stock.domain.member.service.impl.MemberFundsServiceImpl;
import com.hm.stock.domain.member.vo.FundsOperateVo;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.execptions.ErrorResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IpoRecordServiceImpl extends ServiceImpl<IpoRecordMapper, IpoRecord> implements IpoRecordService {
    @Autowired
    private MemberFundsService memberFundsService;
    @Autowired
    private MarketMapper marketMapper;


    @Override
    public List<IpoRecord> selectList(IpoRecord query) {
        QueryWrapper<IpoRecord> ew = new QueryWrapper<>();
        query.setMemberId(SessionInfo.getInstance().getId());
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    public PageDate<IpoRecord> selectByPage(IpoRecord query, PageParam page) {
        QueryWrapper<IpoRecord> ew = new QueryWrapper<>();
        query.setMemberId(SessionInfo.getInstance().getId());
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<IpoRecord> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public IpoRecord detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(IpoRecord body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(IpoRecord body) {
        return updateById(body);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean payment(Long id) {
        Member member = SessionInfo.getMember();

        IpoRecord ipoRecord = detail(id);
        LogicUtils.assertNotNull(ipoRecord, ErrorResultCode.E000029);
        LogicUtils.assertEquals(ipoRecord.getStatus(),"2", ErrorResultCode.E000030);
        LogicUtils.assertEquals(member.getId(),ipoRecord.getMemberId(), ErrorResultCode.E000001);
        LogicUtils.assertNotEquals(ipoRecord.getTotalAmt(),ipoRecord.getTransferAmt(), ErrorResultCode.E000031);

        Market market = marketMapper.selectById(ipoRecord.getMarketId());
        LogicUtils.assertNotNull(market, ErrorResultCode.E000001);

        BigDecimal payment = ipoRecord.getTotalAmt().subtract(ipoRecord.getTransferAmt());

        MemberFunds memberFunds = memberFundsService.getFundsRecord(market, member);
        BigDecimal enableAmt = memberFunds.getEnableAmt();

        if (payment.compareTo(enableAmt) > 0) {
            payment = enableAmt;
        }

        FundsOperateVo fundsOperateVo = new FundsOperateVo();
        fundsOperateVo.setSource(FundsSourceEnum.IPO);
        fundsOperateVo.setOperateType(FundsOperateTypeEnum.FREEZE);
        fundsOperateVo.setMemberId(member.getId());
        fundsOperateVo.setAmt(payment);
        fundsOperateVo.setMarketId(market.getId());
        fundsOperateVo.setNegative(false);
        fundsOperateVo.setSourceId(ipoRecord.getId());
        fundsOperateVo.setName(ipoRecord.getName());
        fundsOperateVo.setCode(ipoRecord.getCode());
        fundsOperateVo.set(FundsOperateVo.FundsInfoKey.STOCK_NAME, ipoRecord.getName())
                .set(FundsOperateVo.FundsInfoKey.STOCK_CODE, ipoRecord.getName())
                .set(FundsOperateVo.FundsInfoKey.FREEZE_AMT, payment)
                .build();

        memberFundsService.addFreezeAmt(fundsOperateVo);

        LogicUtils.assertTrue(baseMapper.addTransferAmt(ipoRecord.getId(), payment) == 1,
                ErrorResultCode.E000001);
        return true;
    }
}
