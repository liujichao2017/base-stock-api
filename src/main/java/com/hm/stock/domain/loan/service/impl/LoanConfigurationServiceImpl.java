package com.hm.stock.domain.loan.service.impl;

import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.auth.entity.SessionInfo;
import com.hm.stock.domain.loan.entity.LoanConfiguration;
import com.hm.stock.domain.loan.entity.MemberLoanRecord;
import com.hm.stock.domain.loan.mapper.LoanConfigurationMapper;
import com.hm.stock.domain.loan.mapper.MemberLoanRecordMapper;
import com.hm.stock.domain.loan.service.LoanConfigurationService;
import com.hm.stock.domain.loan.vo.LoanApplyVo;
import com.hm.stock.domain.loan.vo.LoanConfigVo;
import com.hm.stock.domain.market.entity.Market;
import com.hm.stock.domain.market.mapper.MarketMapper;
import com.hm.stock.domain.member.entity.Member;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.execptions.ErrorResultCode;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class LoanConfigurationServiceImpl extends ServiceImpl<LoanConfigurationMapper, LoanConfiguration> implements LoanConfigurationService {
    private final MarketMapper marketMapper;
    private final MemberLoanRecordMapper memberLoanRecordMapper;

    public LoanConfigurationServiceImpl(MarketMapper marketMapper, MemberLoanRecordMapper memberLoanRecordMapper) {
        this.marketMapper = marketMapper;
        this.memberLoanRecordMapper = memberLoanRecordMapper;
    }

    @Override
    public List<LoanConfiguration> selectList(LoanConfiguration query) {
        QueryWrapper<LoanConfiguration> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    public PageDate<LoanConfiguration> selectByPage(LoanConfiguration query, PageParam page) {
        QueryWrapper<LoanConfiguration> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<LoanConfiguration> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public LoanConfigVo detail(Long marketId) {
        Market market = marketMapper.selectById(marketId);
        LogicUtils.assertNotNull(market, ErrorResultCode.E000003);

        QueryWrapper<LoanConfiguration> ew = new QueryWrapper<>();
        ew.eq("market_id", market.getId());
        List<LoanConfiguration> list = list(ew);
        LoanConfiguration loanConfiguration;
        if (LogicUtils.isNotEmpty(list)) {
            loanConfiguration = list.get(0);
        } else {
            loanConfiguration = new LoanConfiguration();
            loanConfiguration.setMarketId(market.getId());
            loanConfiguration.setMinAmt(new BigDecimal("0"));
            loanConfiguration.setMaxAmt(new BigDecimal("0"));
            loanConfiguration.setInterestRate(new BigDecimal("0"));
            LogicUtils.assertTrue(save(loanConfiguration), ErrorResultCode.E000001);
        }

        LoanConfigVo loanConfigVo = new LoanConfigVo();
        BeanUtils.copyProperties(loanConfiguration, loanConfigVo);
        loanConfigVo.setLoanAmt(SessionInfo.getMember().getLoanAmt());
        return loanConfigVo;
    }

    @Override
    public Long add(LoanConfiguration body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(LoanConfiguration body) {
        return updateById(body);
    }

    @Override
    public Boolean apply(LoanApplyVo body) {
        LoanConfigVo loanConfiguration = detail(body.getId());
        LogicUtils.assertNotNull(loanConfiguration, ErrorResultCode.E000028);

        Market market = marketMapper.selectById(loanConfiguration.getMarketId());
        LogicUtils.assertNotNull(market, ErrorResultCode.E000001);

        Member member = SessionInfo.getMember();

        BigDecimal amt = body.getAmt();
        LogicUtils.assertTrue(loanConfiguration.getMinAmt().compareTo(amt) < 0, ErrorResultCode.E000032);
        LogicUtils.assertTrue(loanConfiguration.getMaxAmt().compareTo(amt) > 0, ErrorResultCode.E000033);

//        BigDecimal applyLoanAmt = memberLoanRecordMapper.countApplyLoanAmt(market.getId(), member.getId());
        BigDecimal applyLoanAmt = BigDecimal.ZERO;
        BigDecimal passLoanAmt = memberLoanRecordMapper.countPassLoanAmt(market.getId(), member.getId());
        LogicUtils.assertTrue(member.getLoanAmt().compareTo(applyLoanAmt.add(passLoanAmt).add(amt)) >= 0,
                ErrorResultCode.E000034);

        MemberLoanRecord memberLoanRecord = new MemberLoanRecord();
        memberLoanRecord.setMarketId(market.getId());
        memberLoanRecord.setMemberId(member.getId());
        memberLoanRecord.setLoanAmount(amt);
        memberLoanRecord.setInterestRate(loanConfiguration.getInterestRate());
        memberLoanRecord.setPassAmount(new BigDecimal("0"));
        memberLoanRecord.setApplyTime(new Date());
        memberLoanRecord.setStatus(1L);
        LogicUtils.assertTrue(memberLoanRecordMapper.insert(memberLoanRecord) == 1, ErrorResultCode.E000001);

        return true;
    }
}
