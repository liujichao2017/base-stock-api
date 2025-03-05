package com.hm.stock.domain.loan.vo;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanConfigVo {
    @Schema(title = "贷款配置ID")
    @Parameter(description = "贷款配置ID")
    private Long id;

    @Schema(title = "市场ID")
    @Parameter(description = "市场ID")
    private Long marketId;

    @Schema(title = "最低借款金额")
    @Parameter(description = "最低借款金额")
    private BigDecimal minAmt;

    @Schema(title = "最高借款金额")
    @Parameter(description = "最高借款金额")
    private BigDecimal maxAmt;

    @Schema(title = "用户的贷款额度")
    @Parameter(description = "用户的贷款额度")
    private BigDecimal loanAmt;

    @Schema(title = "日利率")
    @Parameter(description = "日利率")
    private BigDecimal interestRate;
}
