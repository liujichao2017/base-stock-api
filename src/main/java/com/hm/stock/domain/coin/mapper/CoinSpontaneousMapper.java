package com.hm.stock.domain.coin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hm.stock.domain.coin.entity.CoinSpontaneous;
import org.apache.ibatis.annotations.Select;

/**
 * Mapper
 */
public interface CoinSpontaneousMapper extends BaseMapper<CoinSpontaneous> {

    @Select("select * from coin_spontaneous where symbol = #{symbol} limit 1")
    CoinSpontaneous selectBySymbol(String symbol);
}
