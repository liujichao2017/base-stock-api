package com.hm.stock.domain.coin.vo;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
public class BuyCoinContractVo {

    @Schema(title = "交易对")
    @Parameter(description = "交易对")
    @NotBlank
    private String symbol;

    @Schema(title = "方向: 1:开多, 2:开空")
    @Parameter(description = "方向: 1:开多, 2:开空")
    @NotBlank
    private Long direction;

    @Schema(title = "状态: 1. 限价, 2: 市价")
    @Parameter(description = "状态: 1. 限价, 2: 市价")
    @NotBlank
    private String priceType;

    @Schema(title = "限价委托方向: 1: 现价大于等于委托价. 2: 现价小于等于委托价")
    @Parameter(description = "触发模式: 1: 现价大于等于委托价. 2: 现价小于等于委托价")
    private String limitPriceDirection;

    @Schema(title = "限价")
    @Parameter(description = "限价")
    private BigDecimal limitPrice;

    @Schema(title = "交割类型: 1: 永续, 2: 交割")
    @Parameter(description = "交割类型: 1: 永续, 2: 交割")
    @NotBlank
    private String deliveryType;

    @Schema(title = "交割配置配置: 空为永续")
    @Parameter(description = "交割配置配置: 空为永续")
    private Long deliveryId;

    @Schema(title = "张数")
    @Parameter(description = "张数")
    @NotBlank
    private Long sheetNum;

    @Schema(title = "杠杆")
    @Parameter(description = "杠杆")
    @NotBlank
    private Long level;

    @Schema(title = "止盈价格")
    @Parameter(description = "止盈价格")
    private BigDecimal stopProfitAmt;

    @Schema(title = "止损价格")
    @Parameter(description = "止损价格")
    private BigDecimal stopLossAmt;
}
