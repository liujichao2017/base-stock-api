package com.hm.stock.domain.coin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hm.stock.domain.coin.entity.CoinDelegation;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
* 虚拟币委托任务 Mapper
*/
public interface CoinDelegationMapper extends BaseMapper<CoinDelegation> {

    @Select("select * from coin_delegation where symbol = #{symbol} and trigger_model = '1' and #{last} >= price")
    List<CoinDelegation> selectGePrice(@Param("symbol") String symbol,@Param("last") BigDecimal price);

    @Select("select * from coin_delegation where symbol = #{symbol} and trigger_model = '2' and #{last} <= price")
    List<CoinDelegation> selectLePrice(@Param("symbol") String symbol,@Param("last") BigDecimal price);

    @Delete("delete from coin_delegation where contract_id = #{contractId}")
    long deleteByContractLimit(@Param("contractId") Long contractId);


    @Delete("delete from coin_delegation where contract_delegation_id = #{contractDelegationId}")
    long deleteByContractDelegation(@Param("contractDelegationId") Long contractDelegationId);

    @Select("select * from coin_delegation where trigger_model = '3' and delivery_time  <= #{time}")
    List<CoinDelegation> selectDelivery(@Param("time") long time);
}
