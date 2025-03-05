package com.hm.stock.domain.fund.service.impl;
import com.hm.stock.domain.fund.entity.MemberFundInterestRecord;
import com.hm.stock.domain.fund.mapper.MemberFundInterestRecordMapper;
import com.hm.stock.domain.fund.service.MemberFundRecordService;
import com.hm.stock.domain.member.enums.FundsSourceEnum;
import com.hm.stock.domain.member.enums.FundsOperateTypeEnum;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.auth.entity.SessionInfo;
import com.hm.stock.domain.fund.entity.FundLists;
import com.hm.stock.domain.fund.entity.MemberFundRecord;
import com.hm.stock.domain.fund.mapper.FundListsMapper;
import com.hm.stock.domain.fund.mapper.MemberFundRecordMapper;
import com.hm.stock.domain.fund.service.FundListsService;
import com.hm.stock.domain.fund.vo.FundListsVo;
import com.hm.stock.domain.market.entity.Market;
import com.hm.stock.domain.market.mapper.MarketMapper;
import com.hm.stock.domain.member.entity.Member;
import com.hm.stock.domain.member.service.MemberFundsService;
import com.hm.stock.domain.member.vo.FundsOperateVo;
import com.hm.stock.domain.stock.service.StockClosedService;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.execptions.ErrorResultCode;
import com.hm.stock.modules.utils.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FundListsServiceImpl extends ServiceImpl<FundListsMapper, FundLists> implements FundListsService {
    @Autowired
    private  MarketMapper marketMapper;
    @Autowired
    private StockClosedService stockClosedService;
    @Autowired
    private MemberFundRecordMapper memberFundRecordMapper;
    @Autowired
    private MemberFundsService memberFundsService;
    @Autowired
    private MemberFundInterestRecordMapper memberFundInterestRecordMapper;
    @Autowired
    private MemberFundRecordService memberFundRecordService;

    @Override
    public List<FundLists> selectList(FundLists query) {
        query.setStatus(1L);
        QueryWrapper<FundLists> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    public PageDate<FundLists> selectByPage(FundLists query, PageParam page) {
        query.setStatus(1L);
        QueryWrapper<FundLists> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<FundLists> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public FundLists detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(FundLists body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(FundLists body) {
        return updateById(body);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean buy(FundListsVo fundListsVo) {
        FundLists fundLists = detail(fundListsVo.getId());
        LogicUtils.assertNotNull(fundLists, ErrorResultCode.E000028);

        Long marketId = fundListsVo.getMarketId();
        LogicUtils.assertNotNull(marketId, ErrorResultCode.E000028);

        Market market = marketMapper.selectById(marketId);
        LogicUtils.assertNotNull(market, ErrorResultCode.E000001);

        Member member = SessionInfo.getMember();

        // 交易时间
        LogicUtils.assertFalse(stockClosedService.isClosed(market), ErrorResultCode.E000020);
        LogicUtils.assertTrue(DateTimeUtil.isExpire( fundLists.getStartBuyTime(), fundLists.getEndBuyTime()),
                ErrorResultCode.E000021);

        LogicUtils.assertTrue(fundLists.getMinAmt().compareTo(fundListsVo.getAmt()) <= 0, ErrorResultCode.E000032);
        LogicUtils.assertTrue(fundLists.getMaxAmt().compareTo(fundListsVo.getAmt()) >= 0, ErrorResultCode.E000033);

        MemberFundRecord memberFundRecord = new MemberFundRecord();
        memberFundRecord.setMarketId(market.getId());
        memberFundRecord.setFundId(fundLists.getId());
        memberFundRecord.setMemberId(member.getId());
        memberFundRecord.setAmt(fundListsVo.getAmt());
        memberFundRecord.setStatus("1");
        LogicUtils.assertTrue(memberFundRecordMapper.insert(memberFundRecord) == 1,ErrorResultCode.E000001);

        FundsOperateVo fundsOperateVo = new FundsOperateVo();
        fundsOperateVo.setSource(FundsSourceEnum.FUND);
        fundsOperateVo.setOperateType(FundsOperateTypeEnum.FREEZE);
        fundsOperateVo.setMember(member);
        fundsOperateVo.setMarket(market);
        fundsOperateVo.setAmt(fundListsVo.getAmt());
        fundsOperateVo.setNegative(false);
        fundsOperateVo.setSourceId(memberFundRecord.getId());
        fundsOperateVo.setName(fundLists.getName());
//        fundsOperateVo.setCode(fundLists.getCode());
        fundsOperateVo.set(FundsOperateVo.FundsInfoKey.PRODUCT_NAME, fundLists.getName())
                .set(FundsOperateVo.FundsInfoKey.PRODUCT_CODE, fundLists.getName())
                .set(FundsOperateVo.FundsInfoKey.FREEZE_AMT, fundListsVo.getAmt())
                .build();

        memberFundsService.addFreezeAmt(fundsOperateVo);
        return true;
    }

    @Override
    public Boolean compute(Integer days, String date) {
        String nowDateStr = DateTimeUtil.getNowDateStr();
        QueryWrapper<MemberFundRecord> ew = new QueryWrapper<>();
        ew.eq("status", "2");
        ew.le("pass_time", DateTimeUtil.toStr(days) + " " + date);
        List<MemberFundRecord> list = memberFundRecordService.list(ew);
        if (LogicUtils.isEmpty(list)) {
            return false;
        }
        Set<Long> fundRecordIds = list.stream().map(MemberFundRecord::getId).collect(Collectors.toSet());

        List<MemberFundInterestRecord> memberFundInterestRecords = memberFundInterestRecordMapper.selectLastByUserFundRecordIds(fundRecordIds);
        Map<Long, String> interestRecordMap = memberFundInterestRecords.stream()
                .collect(Collectors.toMap(MemberFundInterestRecord::getUserFundRecordId,MemberFundInterestRecord::getComputeDate));

        QueryWrapper<MemberFundInterestRecord> wrapper = new QueryWrapper<>();

        wrapper.in("user_fund_record_id", fundRecordIds);
        Map<Long, Long> cycleMap = memberFundInterestRecordMapper.selectList(wrapper).stream()
                .collect(Collectors.groupingBy(MemberFundInterestRecord::getUserFundRecordId, Collectors.counting()));

        Map<Long, Market> marketMap = new HashMap<>();
        Map<Market, Boolean> closeMap = new HashMap<>();

        Map<Long, FundLists> fundListsMap = new HashMap<>();
        for (MemberFundRecord memberFundRecord : list) {
            Market market = marketMap.computeIfAbsent(memberFundRecord.getMarketId(), marketMapper::selectById);
            if (closeMap.computeIfAbsent(market, stockClosedService::isClosed)) {
                continue;
            }
            FundLists fundLists = fundListsMap.computeIfAbsent(memberFundRecord.getFundId(), key -> baseMapper.selectById(key));
            if (LogicUtils.isNull(fundLists)) {
                continue;
            }
            String computeDate = interestRecordMap.get(memberFundRecord.getId());
            if (computeDate != null && computeDate.compareTo(nowDateStr) >= 0) {
                continue;
            }
            try {
                memberFundRecordService.compute(fundLists, memberFundRecord, cycleMap.getOrDefault(memberFundRecord.getId(), 0L) + 1);
            } catch (Exception e) {
                log.error("结算基金失败: 基金记录ID: {}", memberFundRecord.getId(), e);
            }
        }
        return true;
    }



}
