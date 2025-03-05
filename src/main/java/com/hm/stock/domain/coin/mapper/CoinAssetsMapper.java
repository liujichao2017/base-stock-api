package com.hm.stock.domain.coin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hm.stock.domain.coin.entity.CoinAssets;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

/**
* 虚拟币资金 Mapper
*/
public interface CoinAssetsMapper extends BaseMapper<CoinAssets> {

    @Update("update coin_assets set num = num + #{num} where num + #{num} >= 0 and id = #{id}")
    int add(@Param("id") Long id, @Param("num") BigDecimal num);

    @Update("update coin_assets set num = num - #{num} where num - #{num} >= 0 and id = #{id}")
    int sub(Long id, BigDecimal num);
}
