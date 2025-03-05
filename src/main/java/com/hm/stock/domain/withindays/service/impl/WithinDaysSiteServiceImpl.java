package com.hm.stock.domain.withindays.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.auth.entity.SessionInfo;
import com.hm.stock.domain.loan.vo.LoanApplyVo;
import com.hm.stock.domain.market.entity.Market;
import com.hm.stock.domain.market.mapper.MarketMapper;
import com.hm.stock.domain.member.entity.Member;
import com.hm.stock.domain.member.enums.FundsOperateTypeEnum;
import com.hm.stock.domain.member.enums.FundsSourceEnum;
import com.hm.stock.domain.member.service.MemberFundsService;
import com.hm.stock.domain.member.vo.FundsOperateVo;
import com.hm.stock.domain.withindays.entity.WithinDaysRecord;
import com.hm.stock.domain.withindays.entity.WithinDaysSite;
import com.hm.stock.domain.withindays.mapper.WithinDaysRecordMapper;
import com.hm.stock.domain.withindays.mapper.WithinDaysSiteMapper;
import com.hm.stock.domain.withindays.service.WithinDaysSiteService;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.execptions.ErrorResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WithinDaysSiteServiceImpl extends ServiceImpl<WithinDaysSiteMapper, WithinDaysSite> implements WithinDaysSiteService {
    @Autowired
    private MarketMapper marketMapper;
    @Autowired
    private MemberFundsService memberFundsService;
    @Autowired
    private WithinDaysRecordMapper withinDaysRecordMapper;

    @Override
    public List<WithinDaysSite> selectList(WithinDaysSite query) {
        QueryWrapper<WithinDaysSite> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    public PageDate<WithinDaysSite> selectByPage(WithinDaysSite query, PageParam page) {
        QueryWrapper<WithinDaysSite> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<WithinDaysSite> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public WithinDaysSite detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(WithinDaysSite body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(WithinDaysSite body) {
        return updateById(body);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean apply(LoanApplyVo body) {
        WithinDaysSite withinDaysSite = detail(body.getId());
        LogicUtils.assertNotNull(withinDaysSite, ErrorResultCode.E000028);

        Market market = marketMapper.selectById(withinDaysSite.getMarketId());
        LogicUtils.assertNotNull(market, ErrorResultCode.E000001);

        Member member = SessionInfo.getMember();

        LogicUtils.assertTrue(withinDaysSite.getMinAmt().compareTo(body.getAmt()) <= 0, ErrorResultCode.E000032);
        LogicUtils.assertTrue(withinDaysSite.getMaxAmt().compareTo(body.getAmt()) >= 0, ErrorResultCode.E000033);

        WithinDaysRecord withinDaysRecord = new WithinDaysRecord();
        withinDaysRecord.setMarketId(market.getId());
        withinDaysRecord.setMemberId(member.getId());
        withinDaysRecord.setAmt(body.getAmt());
        withinDaysRecord.setStatus(1L);

        LogicUtils.assertTrue(withinDaysRecordMapper.insert(withinDaysRecord) == 1, ErrorResultCode.E000001);

        FundsOperateVo fundsOperateVo = new FundsOperateVo();
        fundsOperateVo.setSource(FundsSourceEnum.WITH_DAYS);
        fundsOperateVo.setOperateType(FundsOperateTypeEnum.FREEZE);
        fundsOperateVo.setMember(member);
        fundsOperateVo.setMarket(market);

        fundsOperateVo.setAmt(body.getAmt());
        fundsOperateVo.setNegative(false);
        fundsOperateVo.setSourceId(withinDaysRecord.getId());
        fundsOperateVo.set(FundsOperateVo.FundsInfoKey.OCCUPANCY_AMT, body.getAmt())
                .build();
        memberFundsService.addFreezeAmt(fundsOperateVo);
        return true;
    }
}
