package com.hm.stock.domain.loan.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.lang.Long;
import java.util.Date;
import java.lang.String;

/**
* 用户贷款申请表 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class MemberLoanRecord extends BaseEntity {

    @Schema(title = "市场ID")
    @Parameter(description = "市场ID")
    private Long marketId;

    @Schema(title = "会员Id")
    @Parameter(description = "会员Id")
    private Long memberId;

    @Schema(title = "借款金额")
    @Parameter(description = "借款金额")
    private BigDecimal loanAmount;

    @Schema(title = "日利率")
    @Parameter(description = "日利率")
    private BigDecimal interestRate;

    @Schema(title = "通过金额")
    @Parameter(description = "通过金额")
    private BigDecimal passAmount;

    @Schema(title = "手续费")
    @Parameter(description = "手续费")
    private BigDecimal feeAmt;

    @Schema(title = "申请时间")
    @Parameter(description = "申请时间")
    private Date applyTime;

    @Schema(title = "通过时间")
    @Parameter(description = "通过时间")
    private Date passTime;

    @Schema(title = "状态：1 申请中，2 已通过，3  未通过，4 已结清，5 申请还款")
    @Parameter(description = "状态：1 申请中，2 已通过，3  未通过，4 已结清，5 申请还款")
    private Long status;

    @Schema(title = "还款时间")
    @Parameter(description = "还款时间")
    private Date repaymentTime;

    @Schema(title = "拒绝理由")
    @Parameter(description = "拒绝理由")
    private String rejectContent;

}
