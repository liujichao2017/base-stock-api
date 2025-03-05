package com.hm.stock.domain.loan.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.auth.entity.SessionInfo;
import com.hm.stock.domain.loan.entity.MemberLoanRecord;
import com.hm.stock.domain.loan.mapper.MemberLoanRecordMapper;
import com.hm.stock.domain.loan.service.MemberLoanRecordService;
import com.hm.stock.domain.loan.vo.CountLoanVo;
import com.hm.stock.domain.member.entity.Member;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.execptions.ErrorResultCode;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class MemberLoanRecordServiceImpl extends ServiceImpl<MemberLoanRecordMapper, MemberLoanRecord> implements MemberLoanRecordService {
    @Override
    public List<MemberLoanRecord> selectList(MemberLoanRecord query) {
        QueryWrapper<MemberLoanRecord> ew = new QueryWrapper<>();
        query.setMemberId(SessionInfo.getInstance().getId());
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    public PageDate<MemberLoanRecord> selectByPage(MemberLoanRecord query, PageParam page) {
        QueryWrapper<MemberLoanRecord> ew = new QueryWrapper<>();
        query.setMemberId(SessionInfo.getInstance().getId());
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<MemberLoanRecord> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        if (LogicUtils.isNotEmpty(result.getRecords())) {
            for (MemberLoanRecord memberLoanRecord : result.getRecords()) {
                if (!LogicUtils.isAny(memberLoanRecord.getStatus(), 2L, 5L)) {
                    continue;
                }
                long betweenDay = DateUtil.between(memberLoanRecord.getPassTime(), new Date(), DateUnit.DAY);
                if (memberLoanRecord.getInterestRate().compareTo(BigDecimal.ZERO) <= 0 || betweenDay <= 0) {
                    continue;
                }
                BigDecimal interestRate = memberLoanRecord.getInterestRate().divide(new BigDecimal("100"), 6, 4);
                BigDecimal interest = interestRate.multiply(memberLoanRecord.getPassAmount());
                interest = interest.multiply(BigDecimal.valueOf(betweenDay));
                memberLoanRecord.setFeeAmt(interest);
            }
        }

        return PageDate.of(result);
    }

    @Override
    public MemberLoanRecord detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(MemberLoanRecord body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(MemberLoanRecord body) {
        return updateById(body);
    }

    @Override
    public Boolean repayment(Long id) {
        MemberLoanRecord memberLoanRecord = detail(id);
        LogicUtils.assertNotNull(memberLoanRecord, ErrorResultCode.E000001);
        LogicUtils.assertEquals(memberLoanRecord.getStatus(), 2L, ErrorResultCode.E000035);

        UpdateWrapper<MemberLoanRecord> uw = new UpdateWrapper<>();
        uw.set("status", 5);
        uw.eq("id", id);
        return update(uw);
    }

    @Override
    public Boolean exitsLoan(Long userId) {
        QueryWrapper<MemberLoanRecord> ew = new QueryWrapper<>();
        ew.in("status", "2", "5");
        ew.eq("member_id", SessionInfo.getInstance().getId());
        return LogicUtils.isNotEmpty(baseMapper.selectList(ew));
    }

    @Override
    public CountLoanVo countLoan() {
        Member member = SessionInfo.getMember();
        QueryWrapper<MemberLoanRecord> ew = new QueryWrapper<>();
        ew.in("status", "2", "5");
        ew.eq("member_id", member.getId());
        List<MemberLoanRecord> list = baseMapper.selectList(ew);
        CountLoanVo countLoanVo = new CountLoanVo();
        countLoanVo.setTotal(member.getLoanAmt());
        BigDecimal use = new BigDecimal("0");
        for (MemberLoanRecord memberLoanRecord : list) {
            use = use.add(memberLoanRecord.getPassAmount());
        }
        countLoanVo.setUse(use);
        countLoanVo.setEnable(countLoanVo.getTotal().subtract(use));
        return countLoanVo;
    }
}
