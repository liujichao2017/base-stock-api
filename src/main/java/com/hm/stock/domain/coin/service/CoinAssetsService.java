package com.hm.stock.domain.coin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.coin.entity.CoinAssets;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.math.BigDecimal;
import java.util.List;

public interface CoinAssetsService extends IService<CoinAssets> {
    List<CoinAssets> selectList(CoinAssets query);

    PageDate<CoinAssets> selectByPage(CoinAssets query, PageParam page);

    CoinAssets detail(Long id);

    Long add(CoinAssets body);

    boolean delete(Long id);

    boolean update(CoinAssets body);

    List<CoinAssets> my(Long marketId);

    CoinAssets get(Long marketId, Long memberId, String coin);

    /**
     * 增加
     * @param id
     * @param num
     */
    void plus(Long id, BigDecimal num);

    /**
     * 减少
     * @param id
     * @param num
     */
    void reduce(Long id, BigDecimal num);
}
