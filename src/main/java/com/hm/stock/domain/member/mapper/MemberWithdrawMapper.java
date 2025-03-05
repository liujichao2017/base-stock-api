package com.hm.stock.domain.member.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hm.stock.domain.member.entity.MemberWithdraw;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
* 用户提现表 Mapper
*/
public interface MemberWithdrawMapper extends BaseMapper<MemberWithdraw> {
    @Update("update member_withdraw set status = #{newStatus} where id = #{id} and status = #{oldStatus}")
    int updateStatusById(@Param("id") Long id, @Param("newStatus") String newStatus, @Param("oldStatus") String oldStatus);
}
