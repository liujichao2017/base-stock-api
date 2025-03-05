package com.hm.stock.domain.fund.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hm.stock.domain.fund.entity.MemberFundInterestRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * 基金盈利表 Mapper
 */
public interface MemberFundInterestRecordMapper extends BaseMapper<MemberFundInterestRecord> {


    @Select("select sum(interest) from member_fund_interest_record where user_fund_record_id = #{userFundRecordId}")
    BigDecimal sumInterest(@Param("userFundRecordId") Long id);

    List<MemberFundInterestRecord> selectLastByUserFundRecordIds(@Param("fundRecordIds") Collection<Long> fundRecordIds);
}
