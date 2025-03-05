package com.hm.stock.domain.experience.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hm.stock.domain.experience.entity.Experience;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

/**
* 体验金表 Mapper
*/
public interface ExperienceMapper extends BaseMapper<Experience> {

    @Select("select market_id,sum(amt - use_amt) amt from experience where lock_status = '0'  " +
            "and member_id = #{memberId} and deadline > now() " +
            "and `count` > use_count and amt > use_amt group by market_id")
    List<Experience> sumEnableAmt(@Param("memberId") Long memberId);

    @Select("select * from experience " +
            "where member_id = #{memberId} and market_id = #{marketId} and deadline > now() " +
            "and count > use_count and amt > use_amt and lock_status = '0' order by create_time asc")
    List<Experience> getEnableExperience(@Param("memberId")Long memberId,@Param("marketId") Long marketId);

    @Update("update experience set use_amt = use_amt + #{useAmt},use_count = use_count + 1 " +
            "where lock_status = '0' and use_amt+#{useAmt} <= amt and use_count+1 <= count and deadline >= now() and id = #{id}")
    int addUseAmt(@Param("id")Long id,@Param("useAmt") BigDecimal useAmt);

    @Select("select * from experience where " +
            "member_id = #{memberId} and deadline > now() " +
            "and `count` > use_count and amt > use_amt")
    List<Experience> queryExperience(@Param("memberId") Long memberId);
}
