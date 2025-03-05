package com.hm.stock.domain.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.message.entity.SiteMessageMember;
import com.hm.stock.domain.message.mapper.SiteMessageMemberMapper;
import com.hm.stock.domain.message.service.SiteMessageMemberService;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SiteMessageMemberServiceImpl extends ServiceImpl<SiteMessageMemberMapper, SiteMessageMember> implements SiteMessageMemberService {
    @Override
    public List<SiteMessageMember> selectList(SiteMessageMember query) {
        QueryWrapper<SiteMessageMember> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    public PageDate<SiteMessageMember> selectByPage(SiteMessageMember query, PageParam page) {
        QueryWrapper<SiteMessageMember> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<SiteMessageMember> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public SiteMessageMember detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(SiteMessageMember body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(SiteMessageMember body) {
        return updateById(body);
    }
}
