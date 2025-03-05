package com.hm.stock.domain.fund.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.auth.entity.SessionInfo;
import com.hm.stock.domain.fund.entity.FundInterestRate;
import com.hm.stock.domain.fund.entity.FundLists;
import com.hm.stock.domain.fund.entity.MemberFundInterestRecord;
import com.hm.stock.domain.fund.entity.MemberFundRecord;
import com.hm.stock.domain.fund.mapper.FundInterestRateMapper;
import com.hm.stock.domain.fund.mapper.FundListsMapper;
import com.hm.stock.domain.fund.mapper.MemberFundInterestRecordMapper;
import com.hm.stock.domain.fund.mapper.MemberFundRecordMapper;
import com.hm.stock.domain.fund.service.MemberFundRecordService;
import com.hm.stock.domain.fund.vo.CountMemberFundVo;
import com.hm.stock.domain.fund.vo.MemberFundRecordVo;
import com.hm.stock.domain.market.entity.Market;
import com.hm.stock.domain.market.mapper.MarketMapper;
import com.hm.stock.domain.member.entity.Member;
import com.hm.stock.domain.member.entity.MemberFunds;
import com.hm.stock.domain.member.enums.FundsOperateTypeEnum;
import com.hm.stock.domain.member.enums.FundsSourceEnum;
import com.hm.stock.domain.member.service.MemberFundsService;
import com.hm.stock.domain.member.vo.FundsOperateVo;
import com.hm.stock.domain.stock.service.StockClosedService;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.common.Pair;
import com.hm.stock.modules.enums.YNEnum;
import com.hm.stock.modules.execptions.ErrorResultCode;
import com.hm.stock.modules.utils.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MemberFundRecordServiceImpl extends ServiceImpl<MemberFundRecordMapper, MemberFundRecord> implements MemberFundRecordService {
    @Autowired
    private MemberFundsService memberFundsService;
    @Autowired
    private MemberFundInterestRecordMapper memberFundInterestRecordMapper;
    @Autowired
    private FundListsMapper fundListsMapper;
    @Autowired
    private StockClosedService stockClosedService;
    @Autowired
    private MarketMapper marketMapper;
    @Autowired
    private MemberFundRecordMapper memberFundRecordMapper;
    @Autowired
    private FundInterestRateMapper fundInterestRateMapper;

    @Override
    public List<MemberFundRecord> selectList(MemberFundRecord query) {
        QueryWrapper<MemberFundRecord> ew = new QueryWrapper<>();
        query.setMemberId(SessionInfo.getInstance().getId());
        ew.setEntity(query);
        ew.orderByDesc("id");
        return list(ew);
    }

    @Override
    public PageDate<MemberFundRecordVo> selectByPage(MemberFundRecordVo query, PageParam page) {
        QueryWrapper<MemberFundRecordVo> ew = new QueryWrapper<>();
        query.setMemberId(SessionInfo.getInstance().getId());
        ew.eq(LogicUtils.isNotBlank(query.getFundType()), "fund_lists.type", query.getFundType());
        ew.eq(LogicUtils.isNotBlank(query.getStatus()), "member_fund_record.status", query.getStatus());
        ew.eq("member_fund_record.member_id", SessionInfo.getInstance().getId());
        ew.eq(LogicUtils.isNotNull(query.getMarketId()), "member_fund_record.market_id", query.getMarketId());
        ew.orderByDesc("create_time");
        PageDTO<MemberFundRecordVo> result = baseMapper.selectByPage(
                new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        if (LogicUtils.isNotEmpty(result.getRecords())) {
            QueryWrapper<MemberFundInterestRecord> ewInterestRecord = new QueryWrapper<>();
            ewInterestRecord.in("user_fund_record_id", result.getRecords().stream().map(MemberFundRecordVo::getId)
                    .collect(Collectors.toList()));
            List<MemberFundInterestRecord> memberFundInterestRecords =
                    memberFundInterestRecordMapper.selectList(ewInterestRecord);
            if (LogicUtils.isNotEmpty(memberFundInterestRecords)) {
                Map<Long, Pair<Long, BigDecimal>> map = new HashMap<>();
                memberFundInterestRecords.forEach(r -> {
                    Pair<Long, BigDecimal> pair = map.computeIfAbsent(r.getUserFundRecordId(),
                                                                      key -> Pair.of(0L, BigDecimal.ZERO));
                    pair.setKey(pair.getKey() + 1);
                    pair.setValue(pair.getValue().add(r.getInterest()));
                });
                for (MemberFundRecordVo record : result.getRecords()) {
                    Pair<Long, BigDecimal> pair = map.getOrDefault(record.getId(),Pair.of(0L, BigDecimal.ZERO));
                    record.setUseCycle(pair.getKey());
                    record.setIncome(pair.getValue());
                }
            }

        }
        return PageDate.of(result);
    }

    @Override
    public MemberFundRecord detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(MemberFundRecord body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(MemberFundRecord body) {
        return updateById(body);
    }

    @Override
    public List<CountMemberFundVo> countFund() {
        List<CountMemberFundVo> countMemberFundList = new ArrayList<>();
        Member member = SessionInfo.getMember();
        List<MemberFunds> fundsRecords = memberFundsService.getFundsRecords(member);

        QueryWrapper<MemberFundRecord> ew = new QueryWrapper<>();
        ew.eq("member_id", member.getId());
        List<MemberFundRecord> memberFundRecords = list(ew);
        Map<Long, List<MemberFundRecord>> marketFundMap = LogicUtils.groupMap(memberFundRecords,
                                                                              MemberFundRecord::getMarketId);
        for (MemberFunds fundsRecord : fundsRecords) {
            CountMemberFundVo countMemberFundVo = new CountMemberFundVo();
            countMemberFundVo.setMarketId(fundsRecord.getMarketId());
            countMemberFundVo.setEnableAmt(fundsRecord.getEnableAmt());
            BigDecimal income = new BigDecimal("0");
            BigDecimal occupancyAmt = new BigDecimal("0");
            BigDecimal arrivalIncome = new BigDecimal("0");

            List<MemberFundRecord> records = marketFundMap.get(fundsRecord.getMarketId());

            Map<Long, FundLists> fundListsMap = new HashMap<>();
            if (LogicUtils.isNotEmpty(records)) {
                for (MemberFundRecord record : records) {
                    FundLists fundLists = getFundLists(fundListsMap, record.getFundId());
                    if (LogicUtils.isNull(fundLists)) {
                        continue;
                    }
                    BigDecimal amt = memberFundInterestRecordMapper.sumInterest(record.getId());
                    amt = LogicUtils.isNotNull(amt) ? amt : BigDecimal.ZERO;
                    income = income.add(amt);
                    if ("4".equals(record.getStatus()) || "2".equals(
                            fundLists.getType())) {
                        arrivalIncome = arrivalIncome.add(amt);

                    }
                    if ("1".equals(record.getStatus()) || "2".equals(
                            fundLists.getType())) {
                        occupancyAmt = occupancyAmt.add(record.getAmt());
                    }



                }
            }
            countMemberFundVo.setIncome(income);
            countMemberFundVo.setArrivalIncome(arrivalIncome);
            countMemberFundVo.setOccupancyAmt(occupancyAmt);
            countMemberFundList.add(countMemberFundVo);
        }
        return countMemberFundList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean sell(Long id) {
        MemberFundRecord memberFundRecord = getById(id);
        LogicUtils.assertNotNull(memberFundRecord, ErrorResultCode.E000029);
        FundLists fundLists = fundListsMapper.selectById(memberFundRecord.getFundId());
        LogicUtils.assertNotNull(fundLists, ErrorResultCode.E000001);

        Market market = marketMapper.selectById(memberFundRecord.getMarketId());
        LogicUtils.assertNotNull(market, ErrorResultCode.E000001);
        LogicUtils.assertFalse(stockClosedService.isClosed(market), ErrorResultCode.E000001);

        return sell(fundLists, memberFundRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean sell(FundLists fundLists, MemberFundRecord memberFundRecord) {
        BigDecimal interest = BigDecimal.ZERO;
        BigDecimal profitLossAmt = memberFundInterestRecordMapper.sumInterest(memberFundRecord.getId());
        if ("1".equals(fundLists.getType())) {
            interest = profitLossAmt;
        }
        FundsOperateVo fundsOperateVo2 = new FundsOperateVo();
        fundsOperateVo2.setSource(FundsSourceEnum.FUND);
        fundsOperateVo2.setOperateType(FundsOperateTypeEnum.SELL);
        fundsOperateVo2.setMemberId(memberFundRecord.getMemberId());
        fundsOperateVo2.setMarketId(memberFundRecord.getMarketId());
        fundsOperateVo2.setAmt(memberFundRecord.getAmt());
        fundsOperateVo2.setSourceId(memberFundRecord.getId());
        fundsOperateVo2.setName(fundLists.getName());
//        fundsOperateVo2.setCode(fundLists.getCode());
        fundsOperateVo2.set(FundsOperateVo.FundsInfoKey.PRODUCT_NAME, fundLists.getName())
                .set(FundsOperateVo.FundsInfoKey.PRODUCT_CODE, fundLists.getName())
                .set(FundsOperateVo.FundsInfoKey.PROFIT_LOSS_AMT, profitLossAmt)
                .build();
        memberFundsService.subOccupancyAmt(fundsOperateVo2);

        if (LogicUtils.isNotEquals(BigDecimal.ZERO, interest)) {
            FundsOperateVo fundsOperateVo1 = new FundsOperateVo();
            fundsOperateVo1.setSource(FundsSourceEnum.FUND);
            fundsOperateVo1.setOperateType(FundsOperateTypeEnum.PROFIT_LOSS);
            fundsOperateVo1.setMemberId(memberFundRecord.getMemberId());
            fundsOperateVo1.setMarketId(memberFundRecord.getMarketId());
            fundsOperateVo1.setAmt(interest);
            fundsOperateVo1.setSourceId(memberFundRecord.getId());
            fundsOperateVo1.setName(fundLists.getName());
//            fundsOperateVo1.setCode(fundLists.getCode());
            fundsOperateVo1.setVisible(YNEnum.NO.getType());
            memberFundsService.addProfitAmt(fundsOperateVo1);
        }

        MemberFundRecord update = new MemberFundRecord();
        update.setId(memberFundRecord.getId());
        update.setStatus("4");
        update.setSellTime(new Date());
        LogicUtils.assertEquals(memberFundRecordMapper.updateById(update), 1, ErrorResultCode.E000001);
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean compute(FundLists fundLists, MemberFundRecord memberFundRecord, Long cycle) {
        QueryWrapper<FundInterestRate> ew = new QueryWrapper<>();
        ew.eq("fund_id", fundLists.getId());
        ew.eq("cycle", cycle);
        ew.last("limit 1");
        FundInterestRate fundInterestRate = fundInterestRateMapper.selectOne(ew);
        LogicUtils.assertNotNull(fundInterestRate, ErrorResultCode.E000001);
        BigDecimal interestRate = fundInterestRate.getInterestRate();
        BigDecimal interest = memberFundRecord.getAmt();
        interestRate = interestRate.divide(new BigDecimal("100"), 6, 4);
        interest = interest.multiply(interestRate);

        MemberFundInterestRecord memberFundInterestRecord = new MemberFundInterestRecord();
        memberFundInterestRecord.setUserFundRecordId(memberFundRecord.getId());
        memberFundInterestRecord.setInterestRate(interestRate);
        memberFundInterestRecord.setInterest(interest);
        memberFundInterestRecord.setComputeDate(DateTimeUtil.getNowDateStr());
        memberFundInterestRecordMapper.insert(memberFundInterestRecord);

        if ("2".equals(fundLists.getType())) {
            FundsOperateVo fundsOperateVo = new FundsOperateVo();
            fundsOperateVo.setSource(FundsSourceEnum.FUND);
            fundsOperateVo.setOperateType(FundsOperateTypeEnum.PROFIT_LOSS);
            fundsOperateVo.setMemberId(memberFundRecord.getMemberId());
            fundsOperateVo.setMarketId(memberFundRecord.getMarketId());
            fundsOperateVo.setAmt(memberFundInterestRecord.getInterest());
            fundsOperateVo.setSourceId(memberFundRecord.getId());
            fundsOperateVo.setName(fundLists.getName());
//            fundsOperateVo.setCode(fundLists.getCode());
            memberFundsService.addProfitAmt(fundsOperateVo);
        }
        if (fundLists.getCycle() <= cycle) {
            sell(fundLists, memberFundRecord);
        }
        return true;
    }

    private FundLists getFundLists(Map<Long, FundLists> fundListsMap, Long fundId) {
        FundLists fundLists = fundListsMap.get(fundId);
        if (fundLists == null) {
            fundLists = fundListsMapper.selectById(fundId);
            fundListsMap.put(fundId, fundLists);
        }
        return fundLists;
    }
}
