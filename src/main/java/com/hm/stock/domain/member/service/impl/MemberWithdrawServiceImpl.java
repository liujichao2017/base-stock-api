package com.hm.stock.domain.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.auth.entity.SessionInfo;
import com.hm.stock.domain.loan.service.MemberLoanRecordService;
import com.hm.stock.domain.market.entity.Market;
import com.hm.stock.domain.market.mapper.MarketMapper;
import com.hm.stock.domain.member.entity.Member;
import com.hm.stock.domain.member.entity.MemberBank;
import com.hm.stock.domain.member.entity.MemberWithdraw;
import com.hm.stock.domain.member.enums.FundsOperateTypeEnum;
import com.hm.stock.domain.member.enums.FundsSourceEnum;
import com.hm.stock.domain.member.mapper.MemberBankMapper;
import com.hm.stock.domain.member.mapper.MemberWithdrawMapper;
import com.hm.stock.domain.member.service.MemberFundsService;
import com.hm.stock.domain.member.service.MemberWithdrawService;
import com.hm.stock.domain.member.vo.FundsOperateVo;
import com.hm.stock.domain.member.vo.MemberCancelWithdrawVo;
import com.hm.stock.domain.member.vo.MemberWithdrawVo;
import com.hm.stock.domain.site.entity.DataConfig;
import com.hm.stock.domain.site.service.DataConfigService;
import com.hm.stock.domain.stock.service.StockClosedService;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.common.Pair;
import com.hm.stock.modules.enums.YNEnum;
import com.hm.stock.modules.execptions.ErrorResultCode;
import com.hm.stock.modules.execptions.InternalException;
import com.hm.stock.modules.utils.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MemberWithdrawServiceImpl extends ServiceImpl<MemberWithdrawMapper, MemberWithdraw> implements MemberWithdrawService {

    @Autowired
    private MemberFundsService memberFundsService;
    @Autowired
    private MemberBankMapper memberBankMapper;
    @Autowired
    private MemberLoanRecordService memberLoanRecordService;
    @Autowired
    private DataConfigService dataConfigService;
    @Autowired
    private StockClosedService closedService;
    @Autowired
    private MarketMapper marketMapper;
    @Autowired
    private MemberWithdrawMapper memberWithdrawMapper;

    @Override
    public PageDate<MemberWithdraw> selectByPage(MemberWithdraw query, PageParam page) {
        QueryWrapper<MemberWithdraw> ew = new QueryWrapper<>();
        query.setMemberId(SessionInfo.getInstance().getId());
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<MemberWithdraw> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        List<Long> memberIds = result.getRecords().stream().filter(w -> LogicUtils.isEquals("0", w.getStatus()))
                .map(MemberWithdraw::getMemberId).collect(Collectors.toList());
        if (LogicUtils.isNotEmpty(memberIds)) {
            QueryWrapper<MemberBank> ew1 = new QueryWrapper<>();
            ew1.in("member_id", memberIds);
            List<MemberBank> memberBanks = memberBankMapper.selectList(ew1);
            Map<Long, MemberBank> memberBankMap = memberBanks.stream()
                    .collect(Collectors.toMap(MemberBank::getMemberId, b -> b));
            for (MemberWithdraw w : result.getRecords()) {
                MemberBank memberBank = memberBankMap.get(w.getMemberId());
                if (LogicUtils.isEquals("0", w.getStatus()) && LogicUtils.isNotNull(memberBank)) {
                    setBank(w, memberBank);
                }
            }
        }
        return PageDate.of(result);
    }

    @Override
    public MemberWithdraw detail(Long id) {
        return getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(MemberWithdrawVo body) {
        Member member = SessionInfo.getMember();
        QueryWrapper<MemberBank> ew = new QueryWrapper<>();
        ew.eq("member_id", member.getId());
        LogicUtils.assertTrue(memberBankMapper.selectCount(ew) == 1, ErrorResultCode.E000014);

        LogicUtils.assertTrue(
                LogicUtils.isBlank(body.getWithPwd()) || LogicUtils.isEquals(member.getWithPwd(), body.getWithPwd()),
                ErrorResultCode.E000052);

        Market market = marketMapper.selectById(body.getMarketId());
        LogicUtils.assertNotNull(market, ErrorResultCode.E000001);

        checkWithdrawal(market, body);


        LogicUtils.assertFalse(memberLoanRecordService.exitsLoan(member.getId()), ErrorResultCode.E000054);


        MemberWithdraw memberWithdraw = new MemberWithdraw();
        memberWithdraw.setMemberId(member.getId());
        memberWithdraw.setMarketId(body.getMarketId());
        memberWithdraw.setOrderSn(LogicUtils.getOrderSn("out"));
        memberWithdraw.setAmt(body.getAmt());
        memberWithdraw.setActualAmt(body.getAmt());
        memberWithdraw.setFee(new BigDecimal("0"));
        memberWithdraw.setStatus("0");
        LogicUtils.assertTrue(save(memberWithdraw), ErrorResultCode.E000001);

        FundsOperateVo fundsOperateVo = new FundsOperateVo();
        fundsOperateVo.setSource(FundsSourceEnum.WITHDRAW);
        fundsOperateVo.setOperateType(FundsOperateTypeEnum.OTHER);
        fundsOperateVo.setMemberId(member.getId());
        fundsOperateVo.setAmt(body.getAmt());
        fundsOperateVo.setMarketId(body.getMarketId());
        fundsOperateVo.setSourceId(memberWithdraw.getId());
        memberFundsService.subEnableAmt(fundsOperateVo);

        return memberWithdraw.getId();
    }

    private void checkWithdrawal(Market market, MemberWithdrawVo body) {
        LogicUtils.assertTrue(YNEnum.yes(market.getIsWithdraw()), ErrorResultCode.E000057);
        LogicUtils.assertFalse(closedService.isClosed(market), ErrorResultCode.E000057);
        List<Pair<String, String>> withdrawalTime = dataConfigService.getWithdrawalTime();
        for (Pair<String, String> pair : withdrawalTime) {
            if (DateTimeUtil.isExpire(pair.getKey(), pair.getValue())) {
                DataConfig defaultValue = new DataConfig();
                defaultValue.setGroup("withdrawal");
                defaultValue.setType("str");
                defaultValue.setKey("min_amt");
                defaultValue.setVal("100");
                defaultValue.setRemark("最低提款金额: 数字");
                String byStr = dataConfigService.getByStr(defaultValue);
                LogicUtils.assertTrue(body.getAmt().compareTo(new BigDecimal(byStr)) >= 0, ErrorResultCode.E000058,
                        byStr);
                return;
            }
        }
        throw new InternalException(ErrorResultCode.E000057);
    }

    private static void setBank(MemberWithdraw update, MemberBank memberBank) {
        update.setBank1(memberBank.getBank1());
        update.setBank2(memberBank.getBank2());
        update.setBank3(memberBank.getBank3());
        update.setBank4(memberBank.getBank4());
        update.setBank5(memberBank.getBank5());
        update.setBank6(memberBank.getBank6());
        update.setBank7(memberBank.getBank7());
        update.setBank8(memberBank.getBank8());
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long userCancel(MemberCancelWithdrawVo body) {
        // 验证参数
        LogicUtils.assertTrue(LogicUtils.isNotNull(body.getId()), ErrorResultCode.E000059);

        // 验证记录是否存在
        MemberWithdraw memberWithdraw = memberWithdrawMapper.selectById(body.getId());
        LogicUtils.assertNotNull(memberWithdraw, ErrorResultCode.E000059);
        // 如果不是待审核状态，不能修改
        LogicUtils.assertEquals(memberWithdraw.getStatus(), "0", ErrorResultCode.E000060);

        // 修改提现状态
        MemberWithdraw update = new MemberWithdraw();
        update.setId(body.getId());
        update.setStatus("3");
        int i = memberWithdrawMapper.updateById(update);

        // 返还用户余额
        if (i > 0) {
            FundsOperateVo fundsOperateVo = new FundsOperateVo();
            fundsOperateVo.setSource(FundsSourceEnum.WITHDRAW);
            fundsOperateVo.setOperateType(FundsOperateTypeEnum.ROLLBACK);
            fundsOperateVo.setMemberId(memberWithdraw.getMemberId());
            fundsOperateVo.setAmt(memberWithdraw.getAmt());
            fundsOperateVo.setMarketId(memberWithdraw.getMarketId());
            fundsOperateVo.setSourceId(memberWithdraw.getId());
            memberFundsService.addEnableAmt(fundsOperateVo);
            return (long) i;
        }
        return 0L;
    }
}
