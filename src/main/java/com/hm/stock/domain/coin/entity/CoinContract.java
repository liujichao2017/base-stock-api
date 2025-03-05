package com.hm.stock.domain.coin.entity;

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
* 虚拟币合约 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class CoinContract extends BaseEntity {

    @Schema(title = "客户ID")
    @Parameter(description = "客户ID")
    private Long memberId;

    @Schema(title = "合约委托ID")
    @Parameter(description = "合约委托ID")
    private Long contractDelegationId;

    @Schema(title = "交易对")
    @Parameter(description = "交易对")
    private String symbol;

    @Schema(title = "基础币种显示名称")
    @Parameter(description = "基础币种显示名称")
    private String bcdn;

    @Schema(title = "计价币种显示名称")
    @Parameter(description = "计价币种显示名称")
    private String qcdn;

    @Schema(title = "买入价")
    @Parameter(description = "买入价")
    private BigDecimal buyAmt;

    @Schema(title = "买入时间")
    @Parameter(description = "买入时间")
    private Date buyTime;

    @Schema(title = "卖出价")
    @Parameter(description = "卖出价")
    private BigDecimal sellAmt;

    @Schema(title = "卖出时间")
    @Parameter(description = "卖出时间")
    private Date sellTime;

    @Schema(title = "币数")
    @Parameter(description = "币数")
    private BigDecimal coinNum;

    @Schema(title = "张数")
    @Parameter(description = "张数")
    private Long sheetNum;

    @Schema(title = "杠杆")
    @Parameter(description = "杠杆")
    private Long level;

    @Schema(title = "方向: 1:开多, 2:开空")
    @Parameter(description = "方向: 1:开多, 2:开空")
    private Long direction;

    @Schema(title = "总价")
    @Parameter(description = "总价")
    private BigDecimal totalAmt;

    @Schema(title = "保证金")
    @Parameter(description = "保证金")
    private BigDecimal marginAmt;

    @Schema(title = "手续费")
    @Parameter(description = "手续费")
    private BigDecimal feeAmt;

    @Schema(title = "盈利资金")
    @Parameter(description = "盈利资金")
    private BigDecimal profitAmt;

    @Schema(title = "止盈价格")
    @Parameter(description = "止盈价格")
    private BigDecimal stopProfitAmt;

    @Schema(title = "止损价格")
    @Parameter(description = "止损价格")
    private BigDecimal stopLossAmt;

    @Schema(title = "强制平仓")
    @Parameter(description = "强制平仓")
    private BigDecimal forcedSellAmt;

    @Schema(title = "交割方案ID")
    @Parameter(description = "交割方案ID")
    private Long deliveryId;

    @Schema(title = "交割时间:为空是永续")
    @Parameter(description = "交割时间:为空是永续")
    private Date deliveryTime;

    @Schema(title = "状态: 1. 持仓, 2: 平仓")
    @Parameter(description = "状态: 1. 持仓, 2: 平仓")
    private String status;

}
