package com.hm.stock.domain.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hm.stock.domain.stock.entity.MemberPosition;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

/**
* 用户持仓表 Mapper
*/
public interface MemberPositionMapper extends BaseMapper<MemberPosition> {

    /**
     * 查询持仓浮动收益总和
     *
     * @param memberId 会员ID
     * @return 浮动收益总和
     */
    @Select("SELECT " +
            "    SUM(CASE " +
            "        WHEN p.direction = '1' THEN (o.price - p.buy_order_price) * p.num " +
            "        WHEN p.direction = '2' THEN (p.buy_order_price - o.price) * p.num " +
            "        ELSE 0 " +
            "    END) as floating_profit " +
            "FROM " +
            "    member_position p " +
            "INNER JOIN " +
            "    otc o " +
            "ON " +
            "    p.stock_gid = o.stock_gid " +
            "WHERE " +
            "    p.member_id = #{memberId} " +
            "    AND p.position_type = '2' " +
            "    AND p.sell_order_time IS NULL")
    BigDecimal selectFloatingProfit(@Param("memberId") Long memberId);
}
