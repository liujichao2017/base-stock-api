package com.hm.stock.domain.ipo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.ipo.entity.IpoRecord;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface IpoRecordService extends IService<IpoRecord> {
    List<IpoRecord> selectList(IpoRecord query);

    PageDate<IpoRecord> selectByPage(IpoRecord query, PageParam page);

    IpoRecord detail(Long id);

    Long add(IpoRecord body);

    boolean delete(Long id);

    boolean update(IpoRecord body);

    Boolean payment(Long id);
}
