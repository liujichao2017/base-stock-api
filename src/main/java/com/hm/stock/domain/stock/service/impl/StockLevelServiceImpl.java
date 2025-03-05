package com.hm.stock.domain.stock.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.auth.entity.SessionInfo;
import com.hm.stock.domain.member.entity.Member;
import com.hm.stock.domain.stock.entity.StockLevel;
import com.hm.stock.domain.stock.mapper.StockLevelMapper;
import com.hm.stock.domain.stock.service.StockLevelService;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockLevelServiceImpl extends ServiceImpl<StockLevelMapper, StockLevel> implements StockLevelService {
    @Override
    public List<StockLevel> selectList(StockLevel query) {
        QueryWrapper<StockLevel> ew = new QueryWrapper<>();
        query.setStatus("1");
        ew.setEntity(query);
        ew.orderByAsc("level");
        List<StockLevel> list = list(ew);
        if (LogicUtils.isNotEmpty(list)) {
            Member member = SessionInfo.getMember();
            String levelItem = member.getLevelItem();
            levelItem = LogicUtils.isBlank(levelItem) ? "[1]" : levelItem;
            List<Long> levelArr = JSONArray.parseArray(levelItem, Long.class);
            for (StockLevel stockLevel : list) {
                if (levelArr.contains(stockLevel.getLevel())) {
                    stockLevel.setStatus("1");
                } else {
                    stockLevel.setStatus("0");
                }
            }
        }
        return list;
    }

    @Override
    public PageDate<StockLevel> selectByPage(StockLevel query, PageParam page) {
        QueryWrapper<StockLevel> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<StockLevel> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public StockLevel detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(StockLevel body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(StockLevel body) {
        return updateById(body);
    }
}
