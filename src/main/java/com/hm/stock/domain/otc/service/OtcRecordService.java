package com.hm.stock.domain.otc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.otc.entity.OtcRecord;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface OtcRecordService extends IService<OtcRecord> {
    List<OtcRecord> selectList(OtcRecord query);

    PageDate<OtcRecord> selectByPage(OtcRecord query, PageParam page);

    OtcRecord detail(Long id);

    Long add(OtcRecord body);

    boolean delete(Long id);

    boolean update(OtcRecord body);
}
