package com.hm.stock.domain.loan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hm.stock.domain.loan.entity.MemberLoanRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

/**
* 用户贷款申请表 Mapper
*/
public interface MemberLoanRecordMapper extends BaseMapper<MemberLoanRecord> {

    @Select("select ifnull(sum(pass_amount),0) from member_loan_record where market_id = #{marketId} and member_id = #{memberId} and status in (2,5)")
    BigDecimal countPassLoanAmt(@Param("marketId") Long marketId, @Param("memberId") Long memberId);

    @Select("select ifnull(sum(loan_amount),0)  from member_loan_record where market_id = #{marketId} and member_id = #{memberId} and status  = 1")
    BigDecimal countApplyLoanAmt(@Param("marketId") Long marketId, @Param("memberId") Long memberId);
}
