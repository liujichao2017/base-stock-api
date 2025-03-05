package com.hm.stock.domain.coin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hm.stock.domain.coin.entity.CoinSpontaneousRobot;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * Mapper
 */
public interface CoinSpontaneousRobotMapper extends BaseMapper<CoinSpontaneousRobot> {

    @Select("select * from coin_spontaneous_robot where #{last} >= min_price and #{last} <= max_price and cs_id = #{csId} and status = 1 order by priority limit 1")
    CoinSpontaneousRobot scan(@Param("csId") Long csId,@Param("last")  BigDecimal last);

}
