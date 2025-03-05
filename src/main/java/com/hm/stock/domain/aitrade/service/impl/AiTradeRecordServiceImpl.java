package com.hm.stock.domain.aitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.aitrade.entity.AiTradeRecord;
import com.hm.stock.domain.aitrade.mapper.AiTradeRecordMapper;
import com.hm.stock.domain.aitrade.service.AiTradeRecordService;
import com.hm.stock.domain.auth.entity.SessionInfo;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiTradeRecordServiceImpl extends ServiceImpl<AiTradeRecordMapper, AiTradeRecord> implements AiTradeRecordService {
    @Override
    public List<AiTradeRecord> selectList(AiTradeRecord query) {
        QueryWrapper<AiTradeRecord> ew = new QueryWrapper<>();
        query.setMemberId(SessionInfo.getInstance().getId());
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    public PageDate<AiTradeRecord> selectByPage(AiTradeRecord query, PageParam page) {
        QueryWrapper<AiTradeRecord> ew = new QueryWrapper<>();
        query.setMemberId(SessionInfo.getInstance().getId());
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<AiTradeRecord> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public AiTradeRecord detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(AiTradeRecord body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(AiTradeRecord body) {
        return updateById(body);
    }
}
