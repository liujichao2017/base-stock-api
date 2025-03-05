package com.hm.stock.domain.coin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.lang.Long;
import java.lang.String;
import java.util.List;

/**
* 虚拟币合约委托 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class CoinContractDelegation extends BaseEntity {

    @Schema(title = "客户ID")
    @Parameter(description = "客户ID")
    private Long memberId;

    @Schema(title = "交易对")
    @Parameter(description = "交易对")
    private String symbol;

    @Schema(title = "状态: 1. 限价, 2: 市价")
    @Parameter(description = "状态: 1. 限价, 2: 市价")
    private String priceType;

    @Schema(title = "方向: 1:开多, 2:开空")
    @Parameter(description = "方向: 1:开多, 2:开空")
    private Long direction;

    @Schema(title = "限价")
    @Parameter(description = "限价")
    private BigDecimal limitPrice;

    @Schema(title = "交割类型: 1: 永续, 2: 交割")
    @Parameter(description = "交割类型: 1: 永续, 2: 交割")
    private String deliveryType;

    @Schema(title = "交割配置配置: 空为永续")
    @Parameter(description = "交割配置配置: 空为永续")
    private Long deliveryId;

    @Schema(title = "币数")
    @Parameter(description = "币数")
    private BigDecimal coinNum;

    @Schema(title = "张数")
    @Parameter(description = "张数")
    private Long sheetNum;

    @Schema(title = "杠杆")
    @Parameter(description = "杠杆")
    private Long level;

    @Schema(title = "总价")
    @Parameter(description = "总价")
    private BigDecimal totalAmt;

    @Schema(title = "保证金")
    @Parameter(description = "保证金")
    private BigDecimal marginAmt;


    @Schema(title = "手续费")
    @Parameter(description = "手续费")
    private BigDecimal feeAmt;

    @Schema(title = "止盈价格")
    @Parameter(description = "止盈价格")
    private BigDecimal stopProfitAmt;

    @Schema(title = "止损价格")
    @Parameter(description = "止损价格")
    private BigDecimal stopLossAmt;

    @Schema(title = "状态: 1. 委托中, 2: 撤销, 3:成交")
    @Parameter(description = "状态: 1. 委托中, 2: 撤销, 3: 异常, 4:成交")
    private String status;


    @Schema(title = "状态数组: 1. 委托中, 2: 撤销, 3:成交")
    @Parameter(description = "状态数组: 1. 委托中, 2: 撤销, 4:成交")
    @TableField(exist = false)
    private List<String> statusList;

}
