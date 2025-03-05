package com.hm.stock.domain.ipo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hm.stock.domain.ipo.entity.IpoRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

/**
* IPO(新股)申购记录表 Mapper
*/
public interface IpoRecordMapper extends BaseMapper<IpoRecord> {

    @Update("update ipo_record set transfer_amt = transfer_amt + #{amt} where id = #{id}")
    int addTransferAmt(@Param("id") Long id,@Param("amt") BigDecimal payment);
}
