package com.hm.stock.domain.fund.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.hm.stock.domain.fund.entity.FundStockRecord;
import org.apache.ibatis.annotations.Param;

/**
* 基金股票子项 Mapper
*/
public interface FundStockRecordMapper extends BaseMapper<FundStockRecord> {

    PageDTO<FundStockRecord> selectByPage(PageDTO<FundStockRecord> objectPageDTO,@Param("ew") QueryWrapper<FundStockRecord> ew);
}
