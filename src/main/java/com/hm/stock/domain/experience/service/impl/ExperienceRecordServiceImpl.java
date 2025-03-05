package com.hm.stock.domain.experience.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.experience.entity.ExperienceRecord;
import com.hm.stock.domain.experience.mapper.ExperienceRecordMapper;
import com.hm.stock.domain.experience.service.ExperienceRecordService;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExperienceRecordServiceImpl extends ServiceImpl<ExperienceRecordMapper, ExperienceRecord> implements ExperienceRecordService {
    @Override
    public List<ExperienceRecord> selectList(ExperienceRecord query) {
        QueryWrapper<ExperienceRecord> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    public PageDate<ExperienceRecord> selectByPage(ExperienceRecord query, PageParam page) {
        QueryWrapper<ExperienceRecord> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<ExperienceRecord> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public ExperienceRecord detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(ExperienceRecord body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(ExperienceRecord body) {
        return updateById(body);
    }
}
