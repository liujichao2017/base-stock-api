package com.hm.stock.domain.market.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hm.stock.domain.market.entity.Market;
import org.apache.ibatis.annotations.Select;

/**
* 市场 Mapper
*/
public interface MarketMapper extends BaseMapper<Market> {

    @Select("select id from market where type = 'coin_usdt'")
    Long selectByUsdtCoinMarketId();

}
