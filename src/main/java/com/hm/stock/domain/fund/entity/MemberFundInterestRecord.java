package com.hm.stock.domain.fund.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.lang.Long;
import java.lang.String;

/**
* 基金盈利表 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class MemberFundInterestRecord extends BaseEntity {

    @Schema(title = "用户购买基金记录ID")
    @Parameter(description = "用户购买基金记录ID")
    private Long userFundRecordId;

    @Schema(title = "日利率")
    @Parameter(description = "日利率")
    private BigDecimal interestRate;

    @Schema(title = "利润")
    @Parameter(description = "利润")
    private BigDecimal interest;

    @Schema(title = "计算时间")
    @Parameter(description = "计算时间")
    private String computeDate;

}
