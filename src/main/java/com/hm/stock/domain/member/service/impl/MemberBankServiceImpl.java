package com.hm.stock.domain.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.auth.entity.SessionInfo;
import com.hm.stock.domain.member.entity.MemberBank;
import com.hm.stock.domain.member.mapper.MemberBankMapper;
import com.hm.stock.domain.member.service.MemberBankService;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.execptions.CommonResultCode;
import com.hm.stock.modules.execptions.ErrorResultCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MemberBankServiceImpl extends ServiceImpl<MemberBankMapper, MemberBank> implements MemberBankService {
    @Override
    public List<MemberBank> selectList(MemberBank query) {
        QueryWrapper<MemberBank> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    public PageDate<MemberBank> selectByPage(MemberBank query, PageParam page) {
        QueryWrapper<MemberBank> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<MemberBank> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public MemberBank detail() {
        SessionInfo instance = SessionInfo.getInstance();
        QueryWrapper<MemberBank> ew = new QueryWrapper<>();
        ew.eq("member_id", instance.getId());
        ew.last("limit 1");
        return getOne(ew);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MemberBank saveByMemberId(MemberBank memberBank) {
        MemberBank detail = detail();
        boolean flag;
        if (detail == null) {
            memberBank.setMemberId(SessionInfo.getInstance().getId());
            flag = save(memberBank);
        }else{
            memberBank.setId(detail.getId());
            flag = updateById(memberBank);
        }
        LogicUtils.assertTrue(flag,CommonResultCode.INTERNAL_ERROR);
        return memberBank;
    }

    @Override
    public Long add(MemberBank body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(MemberBank body) {
        return updateById(body);
    }
}
