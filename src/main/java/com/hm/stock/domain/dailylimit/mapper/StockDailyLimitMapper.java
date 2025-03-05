package com.hm.stock.domain.dailylimit.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.hm.stock.domain.dailylimit.entity.StockDailyLimit;
import io.swagger.v3.oas.annotations.Parameter;
import org.apache.ibatis.annotations.Param;

/**
* 涨停表 Mapper
*/
public interface StockDailyLimitMapper extends BaseMapper<StockDailyLimit> {
}
