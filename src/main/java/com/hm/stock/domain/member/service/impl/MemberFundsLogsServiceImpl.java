package com.hm.stock.domain.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.member.entity.MemberFundsLogs;
import com.hm.stock.domain.member.mapper.MemberFundsLogsMapper;
import com.hm.stock.domain.member.service.MemberFundsLogsService;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberFundsLogsServiceImpl extends ServiceImpl<MemberFundsLogsMapper, MemberFundsLogs> implements MemberFundsLogsService {
    @Override
    public List<MemberFundsLogs> selectList(MemberFundsLogs query) {
        QueryWrapper<MemberFundsLogs> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    public PageDate<MemberFundsLogs> selectByPage(MemberFundsLogs query, PageParam page) {
        QueryWrapper<MemberFundsLogs> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<MemberFundsLogs> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public MemberFundsLogs detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(MemberFundsLogs body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(MemberFundsLogs body) {
        return updateById(body);
    }
}
