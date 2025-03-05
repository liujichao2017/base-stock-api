package com.hm.stock.domain.stock.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.market.entity.Market;
import com.hm.stock.domain.stock.entity.Stock;
import com.hm.stock.domain.stock.vo.StockVo;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.Collection;
import java.util.List;

public interface StockService extends IService<Stock> {
    PageDate<Stock> selectByPage(StockVo query, PageParam page);

    Stock detail(String gid);

    String kline(String gid, String type);

    /**
     * 支持code,gid, 先查gid再查code
     *
     * @param code
     * @return
     */
    Stock getStock(String code);

    /**
     * 支持code,gid, 先查gid再查code
     *
     * @param code
     * @return
     */
    Stock getSingeStock(String code);

    /**
     * 根据股票ID查询股票最新价格
     * @param id
     * @return
     */
    Stock getStockByLatest(Long id);

    /**
     * 查询最新价格
     * @param code 支持 code 与 gid
     * @return
     */
    Stock getStockByLatest(String code);
    Stock getStockByLatest(String code,Market market);

    List<Stock> getStockByGids(Collection<String> gids);

    List<Stock> getStockByIds(Collection<Long> ids);

    List<Stock> getStockByCode(Collection<String> coes);

    Stock getStockByWs(String gid);
}
