package com.hm.stock.domain.member.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hm.stock.domain.member.entity.MemberRecharge;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
* 用户充值表 Mapper
*/
public interface MemberRechargeMapper extends BaseMapper<MemberRecharge> {

    @Update("update member_recharge set status = #{newStatus} where id = #{id} and status = #{oldStatus}")
    int updateStatusById(@Param("id") Long id,@Param("newStatus") String newStatus,@Param("oldStatus") String oldStatus);
}
