package com.hm.stock.domain.loan.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.lang.Long;

/**
* 贷款配置 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class LoanConfiguration extends BaseEntity {

    @Schema(title = "市场ID")
    @Parameter(description = "市场ID")
    private Long marketId;

    @Schema(title = "最低借款金额")
    @Parameter(description = "最低借款金额")
    private BigDecimal minAmt;

    @Schema(title = "最高借款金额")
    @Parameter(description = "最高借款金额")
    private BigDecimal maxAmt;

    @Schema(title = "日利率")
    @Parameter(description = "日利率")
    private BigDecimal interestRate;

}
