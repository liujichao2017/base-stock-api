package com.hm.stock.domain.experience.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.experience.entity.ExperienceRecord;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface ExperienceRecordService extends IService<ExperienceRecord> {
    List<ExperienceRecord> selectList(ExperienceRecord query);

    PageDate<ExperienceRecord> selectByPage(ExperienceRecord query, PageParam page);

    ExperienceRecord detail(Long id);

    Long add(ExperienceRecord body);

    boolean delete(Long id);

    boolean update(ExperienceRecord body);
}
