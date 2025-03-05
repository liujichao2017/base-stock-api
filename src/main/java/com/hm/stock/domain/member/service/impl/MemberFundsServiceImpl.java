package com.hm.stock.domain.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.experience.service.ExperienceService;
import com.hm.stock.domain.market.entity.Market;
import com.hm.stock.domain.market.mapper.MarketMapper;
import com.hm.stock.domain.member.entity.Member;
import com.hm.stock.domain.member.entity.MemberFunds;
import com.hm.stock.domain.member.entity.MemberFundsLogs;
import com.hm.stock.domain.member.enums.FundsOperateTypeEnum;
import com.hm.stock.domain.member.mapper.MemberFundsLogsMapper;
import com.hm.stock.domain.member.mapper.MemberFundsMapper;
import com.hm.stock.domain.member.service.MemberFundsService;
import com.hm.stock.domain.member.vo.FundsOperateVo;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.execptions.CommonResultCode;
import com.hm.stock.modules.execptions.ErrorResultCode;
import com.hm.stock.modules.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class MemberFundsServiceImpl extends ServiceImpl<MemberFundsMapper, MemberFunds> implements MemberFundsService {

    @Autowired
    private MemberFundsLogsMapper memberFundsLogsMapper;

    @Autowired
    private MarketMapper marketMapper;

    @Autowired
    private ExperienceService experienceService;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void addEnableAmt(FundsOperateVo fundsOperateVo) {
        Long memberId = fundsOperateVo.getMemberId();
        if (LogicUtils.isNull(memberId)) {
            LogicUtils.assertNotNull(fundsOperateVo.getMember(), ErrorResultCode.E000001);
            memberId = fundsOperateVo.getMember().getId();
        }
        Long marketId = fundsOperateVo.getMarketId();
        if (LogicUtils.isNull(marketId)) {
            LogicUtils.assertNotNull(fundsOperateVo.getMarket(), ErrorResultCode.E000001);
            marketId = fundsOperateVo.getMarket().getId();
        }

        MemberFunds memberFunds = getMemberFunds(memberId, marketId);
        int count = 0;
        BigDecimal amt = fundsOperateVo.getAmt();
        if (fundsOperateVo.isNegative()) {
            count = baseMapper.addEnableAmtByNegative(amt, memberFunds.getId());
        } else {
            count = baseMapper.addEnableAmt(amt, memberFunds.getId());
        }
        LogicUtils.assertTrue(count == 1, ErrorResultCode.E000005);

        createMemberFundsLogs(fundsOperateVo, logs -> {
            logs.setAmt(amt);
            logs.setEnableAmt(memberFunds.getEnableAmt().add(amt));
            logs.setOccupancyAmt(memberFunds.getOccupancyAmt());
            logs.setFreezeAmt(memberFunds.getFreezeAmt());
            logs.setProfitAmt(memberFunds.getProfitAmt());
        });
    }


    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void subEnableAmt(FundsOperateVo fundsOperateVo) {
        Long memberId = fundsOperateVo.getMemberId();
        if (LogicUtils.isNull(memberId)) {
            LogicUtils.assertNotNull(fundsOperateVo.getMember(), ErrorResultCode.E000001);
            memberId = fundsOperateVo.getMember().getId();
        }
        Long marketId = fundsOperateVo.getMarketId();
        if (LogicUtils.isNull(marketId)) {
            LogicUtils.assertNotNull(fundsOperateVo.getMarket(), ErrorResultCode.E000001);
            marketId = fundsOperateVo.getMarket().getId();
        }

        MemberFunds memberFunds = getMemberFunds(memberId, marketId);
        int count = 0;
        BigDecimal amt = fundsOperateVo.getAmt();
        if (fundsOperateVo.isNegative()) {
            count = baseMapper.subEnableAmtByNegative(amt, memberFunds.getId());
        } else {
            count = baseMapper.subEnableAmt(amt, memberFunds.getId());
        }
        LogicUtils.assertTrue(count == 1, ErrorResultCode.E000005);

        createMemberFundsLogs(fundsOperateVo, logs -> {
            logs.setAmt(amt.negate());
            logs.setEnableAmt(memberFunds.getEnableAmt().subtract(amt));
            logs.setOccupancyAmt(memberFunds.getOccupancyAmt());
            logs.setFreezeAmt(memberFunds.getFreezeAmt());
            logs.setProfitAmt(memberFunds.getProfitAmt());
        });
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void addOccupancyAmt(FundsOperateVo fundsOperateVo) {
        Long memberId = fundsOperateVo.getMemberId();
        if (LogicUtils.isNull(memberId)) {
            LogicUtils.assertNotNull(fundsOperateVo.getMember(), ErrorResultCode.E000001);
            memberId = fundsOperateVo.getMember().getId();
        }
        Long marketId = fundsOperateVo.getMarketId();
        if (LogicUtils.isNull(marketId)) {
            LogicUtils.assertNotNull(fundsOperateVo.getMarket(), ErrorResultCode.E000001);
            marketId = fundsOperateVo.getMarket().getId();
        }

        BigDecimal amt = fundsOperateVo.getAmt();
        MemberFunds memberFunds = getMemberFunds(memberId, marketId);


        // 增加体验金逻辑
        BigDecimal experienceAmt = useExperienceAmt(fundsOperateVo);
        BigDecimal enableAmt = amt.subtract(experienceAmt);

        int count = 0;
        if (fundsOperateVo.isNegative()) {
            count = baseMapper.subEnableAmtByNegative(enableAmt, memberFunds.getId());
        } else {
            count = baseMapper.subEnableAmt(enableAmt, memberFunds.getId());
        }
        LogicUtils.assertTrue(LogicUtils.isEquals(enableAmt, BigDecimal.ZERO) || count == 1, ErrorResultCode.E000005);

        count = baseMapper.addOccupancyAmt(amt, memberFunds.getId());
        LogicUtils.assertTrue(count == 1, ErrorResultCode.E000001);

        createMemberFundsLogs(fundsOperateVo, logs -> {
            logs.setAmt(amt.negate());
            logs.setEnableAmt(memberFunds.getEnableAmt().subtract(enableAmt));
            logs.setOccupancyAmt(memberFunds.getOccupancyAmt().add(amt));
            logs.setFreezeAmt(memberFunds.getFreezeAmt());
            logs.setProfitAmt(memberFunds.getProfitAmt());
        });
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void subOccupancyAmt(FundsOperateVo fundsOperateVo) {
        Long memberId = fundsOperateVo.getMemberId();
        if (LogicUtils.isNull(memberId)) {
            LogicUtils.assertNotNull(fundsOperateVo.getMember(), ErrorResultCode.E000001);
            memberId = fundsOperateVo.getMember().getId();
        }
        Long marketId = fundsOperateVo.getMarketId();
        if (LogicUtils.isNull(marketId)) {
            LogicUtils.assertNotNull(fundsOperateVo.getMarket(), ErrorResultCode.E000001);
            marketId = fundsOperateVo.getMarket().getId();
        }

        BigDecimal amt = fundsOperateVo.getAmt();
        MemberFunds memberFunds = getMemberFunds(memberId, marketId);

        // 增加体验金逻辑
        BigDecimal useExperienceAmt = queryExperienceAmt(fundsOperateVo);
        BigDecimal enableAmt = amt.subtract(useExperienceAmt);

        if (fundsOperateVo.getOperateType() == FundsOperateTypeEnum.ROLLBACK) {
            rollbackExperienceAmt(fundsOperateVo);
        }

        int count = 0;
        if (fundsOperateVo.isNegative()) {
            count = baseMapper.addEnableAmtByNegative(enableAmt, memberFunds.getId());
        } else {
            count = baseMapper.addEnableAmt(enableAmt, memberFunds.getId());
        }
        LogicUtils.assertTrue(LogicUtils.isEquals(enableAmt, BigDecimal.ZERO) || count == 1, ErrorResultCode.E000001);

        count = baseMapper.subOccupancyAmt(amt, memberFunds.getId());
        LogicUtils.assertTrue(count == 1, ErrorResultCode.E000001);

        createMemberFundsLogs(fundsOperateVo, logs -> {
            logs.setAmt(amt);
            logs.setEnableAmt(memberFunds.getEnableAmt().add(enableAmt));
            logs.setOccupancyAmt(memberFunds.getOccupancyAmt().subtract(amt));
            logs.setFreezeAmt(memberFunds.getFreezeAmt());
            logs.setProfitAmt(memberFunds.getProfitAmt());
        });
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void addFreezeAmt(FundsOperateVo fundsOperateVo) {
        Long memberId = fundsOperateVo.getMemberId();
        if (LogicUtils.isNull(memberId)) {
            LogicUtils.assertNotNull(fundsOperateVo.getMember(), ErrorResultCode.E000001);
            memberId = fundsOperateVo.getMember().getId();
        }
        Long marketId = fundsOperateVo.getMarketId();
        if (LogicUtils.isNull(marketId)) {
            LogicUtils.assertNotNull(fundsOperateVo.getMarket(), ErrorResultCode.E000001);
            marketId = fundsOperateVo.getMarket().getId();
        }

        BigDecimal amt = fundsOperateVo.getAmt();
        MemberFunds memberFunds = getMemberFunds(memberId, marketId);

        // 增加体验金逻辑
        BigDecimal experienceAmt = useExperienceAmt(fundsOperateVo);
        BigDecimal enableAmt = amt.subtract(experienceAmt);

        int count = 0;
        if (fundsOperateVo.isNegative()) {
            count = baseMapper.subEnableAmtByNegative(enableAmt, memberFunds.getId());
        } else {
            count = baseMapper.subEnableAmt(enableAmt, memberFunds.getId());
        }
        LogicUtils.assertTrue(LogicUtils.isEquals(enableAmt, BigDecimal.ZERO) || count == 1, ErrorResultCode.E000005);

        count = baseMapper.addFreezeAmt(amt, memberFunds.getId());
        LogicUtils.assertTrue(count == 1, ErrorResultCode.E000001);

        createMemberFundsLogs(fundsOperateVo, logs -> {
            logs.setAmt(amt.negate());
            logs.setEnableAmt(memberFunds.getEnableAmt().subtract(enableAmt));
            logs.setOccupancyAmt(memberFunds.getOccupancyAmt());
            logs.setFreezeAmt(memberFunds.getFreezeAmt().add(amt));
            logs.setProfitAmt(memberFunds.getProfitAmt());
        });
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void subFreezeAmt(FundsOperateVo fundsOperateVo) {
        Long memberId = fundsOperateVo.getMemberId();
        if (LogicUtils.isNull(memberId)) {
            LogicUtils.assertNotNull(fundsOperateVo.getMember(), ErrorResultCode.E000001);
            memberId = fundsOperateVo.getMember().getId();
        }
        Long marketId = fundsOperateVo.getMarketId();
        if (LogicUtils.isNull(marketId)) {
            LogicUtils.assertNotNull(fundsOperateVo.getMarket(), ErrorResultCode.E000001);
            marketId = fundsOperateVo.getMarket().getId();
        }

        BigDecimal amt = fundsOperateVo.getAmt();
        MemberFunds memberFunds = getMemberFunds(memberId, marketId);

        // 增加体验金逻辑
        BigDecimal experienceAmt = queryExperienceAmt(fundsOperateVo);
        BigDecimal enableAmt = amt.subtract(experienceAmt);

        if (fundsOperateVo.getOperateType() == FundsOperateTypeEnum.ROLLBACK) {
            rollbackExperienceAmt(fundsOperateVo);
        }

        int count = 0;
        if (fundsOperateVo.isNegative()) {
            count = baseMapper.addEnableAmtByNegative(enableAmt, memberFunds.getId());
        } else {
            count = baseMapper.addEnableAmt(enableAmt, memberFunds.getId());
        }
        LogicUtils.assertTrue(LogicUtils.isEquals(enableAmt, BigDecimal.ZERO) || count == 1, ErrorResultCode.E000001);

        count = baseMapper.subFreezeAmt(amt, memberFunds.getId());
        LogicUtils.assertTrue(count == 1, ErrorResultCode.E000001);


        createMemberFundsLogs(fundsOperateVo, logs -> {
            logs.setAmt(amt);
            logs.setEnableAmt(memberFunds.getEnableAmt().add(enableAmt));
            logs.setOccupancyAmt(memberFunds.getOccupancyAmt());
            logs.setFreezeAmt(memberFunds.getFreezeAmt().subtract(amt));
            logs.setProfitAmt(memberFunds.getProfitAmt());
        });
    }


    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void freezeToOccupancyAmt(FundsOperateVo fundsOperateVo) {
        Long memberId = fundsOperateVo.getMemberId();
        if (LogicUtils.isNull(memberId)) {
            LogicUtils.assertNotNull(fundsOperateVo.getMember(), ErrorResultCode.E000001);
            memberId = fundsOperateVo.getMember().getId();
        }
        Long marketId = fundsOperateVo.getMarketId();
        if (LogicUtils.isNull(marketId)) {
            LogicUtils.assertNotNull(fundsOperateVo.getMarket(), ErrorResultCode.E000001);
            marketId = fundsOperateVo.getMarket().getId();
        }

        BigDecimal amt = fundsOperateVo.getAmt();
        MemberFunds memberFunds = getMemberFunds(memberId, marketId);

        int count = baseMapper.subFreezeAmt(amt, memberFunds.getId());
        LogicUtils.assertTrue(count == 1, ErrorResultCode.E000001);

        count = baseMapper.addOccupancyAmt(amt, memberFunds.getId());
        LogicUtils.assertTrue(count == 1, ErrorResultCode.E000001);

        createMemberFundsLogs(fundsOperateVo, logs -> {
            logs.setAmt(amt);
            logs.setEnableAmt(memberFunds.getEnableAmt());
            logs.setOccupancyAmt(memberFunds.getOccupancyAmt().add(amt));
            logs.setFreezeAmt(memberFunds.getFreezeAmt().subtract(amt));
            logs.setProfitAmt(memberFunds.getProfitAmt());
        });
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void addProfitAmt(FundsOperateVo fundsOperateVo) {
        Long memberId = fundsOperateVo.getMemberId();
        if (LogicUtils.isNull(memberId)) {
            LogicUtils.assertNotNull(fundsOperateVo.getMember(), ErrorResultCode.E000001);
            memberId = fundsOperateVo.getMember().getId();
        }
        Long marketId = fundsOperateVo.getMarketId();
        if (LogicUtils.isNull(marketId)) {
            LogicUtils.assertNotNull(fundsOperateVo.getMarket(), ErrorResultCode.E000001);
            marketId = fundsOperateVo.getMarket().getId();
        }

        BigDecimal amt = fundsOperateVo.getAmt();
        // 体验金操作, 收益为负数, 不扣除本金
        if (amt.compareTo(BigDecimal.ZERO) <= 0 && BigDecimal.ZERO.compareTo(queryExperienceAmt(fundsOperateVo)) < 0) {
            return;
        }
        MemberFunds memberFunds = getMemberFunds(memberId, marketId);

        int count = 0;
        if (fundsOperateVo.isNegative()) {
            count = baseMapper.addEnableAmtByNegative(amt, memberFunds.getId());
        } else {
            count = baseMapper.addEnableAmt(amt, memberFunds.getId());
        }
        LogicUtils.assertTrue(count == 1, ErrorResultCode.E000005);

        count = baseMapper.addProfitAmt(amt, memberFunds.getId());
        LogicUtils.assertTrue(count == 1, ErrorResultCode.E000001);

        createMemberFundsLogs(fundsOperateVo, logs -> {
            logs.setAmt(amt);
            logs.setEnableAmt(memberFunds.getEnableAmt().add(amt));
            logs.setOccupancyAmt(memberFunds.getOccupancyAmt());
            logs.setFreezeAmt(memberFunds.getFreezeAmt());
            logs.setProfitAmt(memberFunds.getProfitAmt().add(amt));
        });
    }

    @Override
    public void subProfitAmt(FundsOperateVo fundsOperateVo) {
        Long memberId = fundsOperateVo.getMemberId();
        if (LogicUtils.isNull(memberId)) {
            LogicUtils.assertNotNull(fundsOperateVo.getMember(), ErrorResultCode.E000001);
            memberId = fundsOperateVo.getMember().getId();
        }
        Long marketId = fundsOperateVo.getMarketId();
        if (LogicUtils.isNull(marketId)) {
            LogicUtils.assertNotNull(fundsOperateVo.getMarket(), ErrorResultCode.E000001);
            marketId = fundsOperateVo.getMarket().getId();
        }

        BigDecimal amt = fundsOperateVo.getAmt();
        MemberFunds memberFunds = getMemberFunds(memberId, marketId);
        int count = 0;
        if (fundsOperateVo.isNegative()) {
            count = baseMapper.subEnableAmtByNegative(amt, memberFunds.getId());
        } else {
            count = baseMapper.subEnableAmt(amt, memberFunds.getId());
        }
        LogicUtils.assertTrue(count == 1, ErrorResultCode.E000005);

        count = baseMapper.subProfitAmt(amt, memberFunds.getId());
        LogicUtils.assertTrue(count == 1, ErrorResultCode.E000001);

        createMemberFundsLogs(fundsOperateVo, logs -> {
            logs.setAmt(amt.negate());
            logs.setEnableAmt(memberFunds.getEnableAmt().subtract(amt));
            logs.setOccupancyAmt(memberFunds.getOccupancyAmt());
            logs.setFreezeAmt(memberFunds.getFreezeAmt());
            logs.setProfitAmt(memberFunds.getProfitAmt().subtract(amt));
        });
    }

    @Override
    public MemberFunds getFundsRecord(Market market, Member member) {
        List<MemberFunds> memberFunds = getMemberFundsList(member.getId());
        if (LogicUtils.isEmpty(memberFunds)) {
            return createMemberFunds(member.getId(), market.getId());
        }
        return memberFunds.get(0);
    }

    @Override
    public List<MemberFunds> getFundsRecords(Member member) {
        List<Market> markets = marketMapper.selectList(new QueryWrapper<>());
        List<MemberFunds> memberFunds = getMemberFundsList(member.getId());
        Set<Long> marketIdSet = memberFunds.stream().map(MemberFunds::getMarketId).collect(Collectors.toSet());
        for (Market market : markets) {
            if (!marketIdSet.contains(market.getId())) {
                memberFunds.add(createMemberFunds(member.getId(), market.getId()));
            }
        }
        return memberFunds;
    }

    private List<MemberFunds> getMemberFundsList(Long memberId) {
        QueryWrapper<MemberFunds> ew = new QueryWrapper<>();
        ew.eq("member_id", memberId);
        List<MemberFunds> memberFundsList = baseMapper.selectList(ew);
        return memberFundsList;
    }

    private MemberFunds getMemberFunds(Long memberId, Long marketId) {
        QueryWrapper<MemberFunds> ew = new QueryWrapper<>();
        ew.eq("member_id", memberId);
        ew.eq(LogicUtils.isNotNull(marketId), "market_id", marketId);
        ew.last("limit 1");
        MemberFunds memberFunds = baseMapper.selectOne(ew);
        if (LogicUtils.isNull(memberFunds)) {
            return createMemberFunds(memberId, marketId);
        }
        return memberFunds;
    }

    private MemberFunds createMemberFunds(Long memberId, Long marketId) {
        MemberFunds memberFunds = new MemberFunds();
        memberFunds.setMemberId(memberId);
        memberFunds.setMarketId(marketId);
        memberFunds.setEnableAmt(BigDecimal.ZERO);
        memberFunds.setFreezeAmt(BigDecimal.ZERO);
        memberFunds.setOccupancyAmt(BigDecimal.ZERO);
        memberFunds.setProfitAmt(BigDecimal.ZERO);
        LogicUtils.assertTrue(save(memberFunds), CommonResultCode.INTERNAL_ERROR);
        return memberFunds;
    }

    private void createMemberFundsLogs(FundsOperateVo fundsOperateVo, Consumer<MemberFundsLogs> consumer) {
        Long memberId = fundsOperateVo.getMemberId();
        if (LogicUtils.isNull(memberId)) {
            memberId = fundsOperateVo.getMember().getId();
        }
        Long marketId = fundsOperateVo.getMarketId();
        if (LogicUtils.isNull(marketId)) {
            marketId = fundsOperateVo.getMarket().getId();
        }
        MemberFundsLogs memberFundsLogs = new MemberFundsLogs();
        memberFundsLogs.setMemberId(memberId);
        memberFundsLogs.setMarketId(marketId);
        memberFundsLogs.setVisible(LogicUtils.isNotBlank(fundsOperateVo.getVisible())
                                           ? fundsOperateVo.getVisible() : fundsOperateVo.getSource().getVisible());
        memberFundsLogs.setSource(fundsOperateVo.getSource().getType());
        memberFundsLogs.setSourceId(fundsOperateVo.getSourceId());
        memberFundsLogs.setOperateType(fundsOperateVo.getOperateType().getType());

        consumer.accept(memberFundsLogs);

        StringBuilder content = new StringBuilder();
        content.append(fundsOperateVo.getSource().getName()).append(" ");
        content.append(fundsOperateVo.getOperateType().getName()).append(" ");
        Map<String, String> contentMap = new HashMap<>();
        if (LogicUtils.isNotBlank(fundsOperateVo.getName())) {
            content.append("名称: ").append(fundsOperateVo.getName());
            contentMap.put(FundsOperateVo.FundsInfoKey.STOCK_NAME, fundsOperateVo.getName());
        }
        if (LogicUtils.isNotBlank(fundsOperateVo.getCode())) {
            content.append("代码: ").append(fundsOperateVo.getCode());
            contentMap.put(FundsOperateVo.FundsInfoKey.STOCK_NAME, fundsOperateVo.getCode());
        }
        memberFundsLogs.setContent(content.toString());
        memberFundsLogs.setContentJson(LogicUtils.isNotBlank(fundsOperateVo.getContentJson())
                                               ? fundsOperateVo.getContentJson() : JsonUtil.toStr(contentMap));
        LogicUtils.assertTrue(memberFundsLogsMapper.insert(memberFundsLogs) == 1, CommonResultCode.INTERNAL_ERROR);
    }


    private BigDecimal queryExperienceAmt(FundsOperateVo fundsOperateVo) {
        if (LogicUtils.isNull(fundsOperateVo.getSource()) || LogicUtils.isNull(fundsOperateVo.getSourceId())) {
            return BigDecimal.ZERO;
        }

        return experienceService.queryUseAmt(fundsOperateVo.getSource(), fundsOperateVo.getSourceId());
    }

    private BigDecimal useExperienceAmt(FundsOperateVo fundsOperateVo) {
        if (LogicUtils.isNull(fundsOperateVo.getSource()) || LogicUtils.isNull(fundsOperateVo.getSourceId())) {
            return BigDecimal.ZERO;
        }

        Long memberId = fundsOperateVo.getMemberId();
        if (LogicUtils.isNull(memberId)) {
            memberId = fundsOperateVo.getMember().getId();
        }
        Long marketId = fundsOperateVo.getMarketId();
        if (LogicUtils.isNull(marketId)) {
            marketId = fundsOperateVo.getMarket().getId();
        }
        return experienceService.use(marketId, memberId, fundsOperateVo.getSource(), fundsOperateVo.getSourceId(),
                                     fundsOperateVo.getAmt());
    }


    private void rollbackExperienceAmt(FundsOperateVo fundsOperateVo) {
        if (LogicUtils.isNull(fundsOperateVo.getSource()) || LogicUtils.isNull(fundsOperateVo.getSourceId())) {
            return;
        }
        experienceService.rollback(fundsOperateVo.getSource(), fundsOperateVo.getSourceId());
    }
}
