package com.hm.stock.domain.coin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hm.stock.domain.coin.entity.CoinSymbols;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
* 虚拟币-交易对 Mapper
*/
public interface CoinSymbolsMapper extends BaseMapper<CoinSymbols> {

    @Select("select `type` from coin_symbols where symbol = #{symbol} limit 1")
    String selectByType(@Param("symbol") String symbol);

    @Select("select * from coin_symbols where symbol = #{symbol} limit 1")
    CoinSymbols selectBySymbol(String symbol);
}
