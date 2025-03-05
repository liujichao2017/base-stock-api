package com.hm.stock.domain.coin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hm.stock.domain.coin.entity.CoinKline;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
*  Mapper
*/
public interface CoinKlineMapper extends BaseMapper<CoinKline> {

    @Select("select * from coin_kline where symbol = #{symbol} and period = #{period} and ts <= #{ts} order by ts desc limit 1")
    CoinKline selectLastKline(@Param("symbol") String symbol,@Param("ts") Long ts,@Param("period") String period);
}
