package com.hm.stock.domain.fund.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.hm.stock.domain.fund.entity.MemberFundRecord;
import com.hm.stock.domain.fund.vo.MemberFundRecordVo;
import org.apache.ibatis.annotations.Param;

/**
* 基金购买记录 Mapper
*/
public interface MemberFundRecordMapper extends BaseMapper<MemberFundRecord> {

    PageDTO<MemberFundRecordVo> selectByPage(PageDTO<Object> page,@Param("ew") QueryWrapper<MemberFundRecordVo> ew);
}
