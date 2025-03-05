package com.hm.stock.domain.aitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.aitrade.entity.AiTrade;
import com.hm.stock.domain.aitrade.vo.AiTradeBuyVo;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface AiTradeService extends IService<AiTrade> {
    List<AiTrade> selectList(AiTrade query);

    PageDate<AiTrade> selectByPage(AiTrade query, PageParam page);

    AiTrade detail(Long id);

    Long add(AiTrade body);

    boolean delete(Long id);

    boolean update(AiTrade body);

    Boolean buy(AiTradeBuyVo body);
}
