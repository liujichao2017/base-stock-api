package com.hm.stock.domain.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.auth.entity.SessionInfo;
import com.hm.stock.domain.member.entity.MemberRecharge;
import com.hm.stock.domain.member.mapper.MemberRechargeMapper;
import com.hm.stock.domain.member.service.MemberRechargeService;
import com.hm.stock.domain.member.vo.MemberRechargeVo;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.execptions.ErrorResultCode;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class MemberRechargeServiceImpl extends ServiceImpl<MemberRechargeMapper, MemberRecharge> implements MemberRechargeService {

    @Override
    public PageDate<MemberRecharge> selectByPage(MemberRecharge query, PageParam page) {
        QueryWrapper<MemberRecharge> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<MemberRecharge> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public Long add(MemberRechargeVo body) {
        SessionInfo instance = SessionInfo.getInstance();
        MemberRecharge memberRecharge = add(body, instance.getId(), "0");
        return memberRecharge.getId();
    }

    private MemberRecharge add(MemberRechargeVo body, Long memberId, String status) {
        MemberRecharge memberRecharge = new MemberRecharge();
        memberRecharge.setMemberId(memberId);
        memberRecharge.setMarketId(body.getMarketId());
        memberRecharge.setOrderSn(LogicUtils.getOrderSn("in"));
        memberRecharge.setAmt(body.getAmt());
        memberRecharge.setActualAmt(body.getAmt());
        memberRecharge.setFee(new BigDecimal("0"));
        memberRecharge.setStatus(status);
        LogicUtils.assertTrue(save(memberRecharge), ErrorResultCode.E000001);
        return memberRecharge;
    }
}
