package com.hm.stock.domain.coin.vo;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CalculateContractParamVo {

    @Schema(title = "交易对")
    @Parameter(description = "交易对")
    private String symbol;

    @Schema(title = "状态: 1. 限价, 2: 市价")
    @Parameter(description = "状态: 1. 限价, 2: 市价")
    private String priceType;

    @Schema(title = "限价")
    @Parameter(description = "限价")
    private BigDecimal limitPrice;

    @Schema(title = "张数")
    @Parameter(description = "张数")
    private Long sheetNum;

    @Schema(title = "杠杆")
    @Parameter(description = "杠杆")
    private Long level;

}
