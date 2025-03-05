package com.hm.stock.domain.aitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.aitrade.entity.AiTradeRecord;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface AiTradeRecordService extends IService<AiTradeRecord> {
    List<AiTradeRecord> selectList(AiTradeRecord query);

    PageDate<AiTradeRecord> selectByPage(AiTradeRecord query, PageParam page);

    AiTradeRecord detail(Long id);

    Long add(AiTradeRecord body);

    boolean delete(Long id);

    boolean update(AiTradeRecord body);
}
