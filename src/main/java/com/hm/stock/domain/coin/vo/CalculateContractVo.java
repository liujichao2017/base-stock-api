package com.hm.stock.domain.coin.vo;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CalculateContractVo {

    @Schema(title = "交易对")
    @Parameter(description = "交易对")
    private String symbol;

    @Schema(title = "总价")
    @Parameter(description = "总价")
    private BigDecimal totalAmt;

    @Schema(title = "保证金")
    @Parameter(description = "保证金")
    private BigDecimal marginAmt;

    @Schema(title = "手续费")
    @Parameter(description = "手续费")
    private BigDecimal feeAmt;

    @Schema(title = "强制平仓")
    @Parameter(description = "强制平仓")
    private BigDecimal forcedSellAmt;

}
