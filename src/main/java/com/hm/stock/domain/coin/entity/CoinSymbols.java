package com.hm.stock.domain.coin.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 虚拟币-交易对 实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CoinSymbols extends BaseEntity {

    @Schema(title = "交易对")
    @Parameter(description = "交易对")
    private String symbol;

    @Schema(title = "交易对状态。 unknown：未知，not-online：未上线，pre-online：预上线，online：已上线，suspend：暂停，offline：已下线，transfer-board：转版，fuse：熔断（风控系统控制）")
    @Parameter(description = "交易对状态。 unknown：未知，not-online：未上线，pre-online：预上线，online：已上线，suspend：暂停，offline：已下线，transfer-board：转版，fuse：熔断（风控系统控制）")
    private String state;

    @Schema(title = "是否显示 0非 1是")
    @Parameter(description = "是否显示 0非 1是")
    private Long hot;

    @Schema(title = "每手币数")
    @Parameter(description = "每手币数")
    private BigDecimal sheet;

    @Schema(title = "手续费")
    @Parameter(description = "手续费")
    private BigDecimal feeAmt;

    @Schema(title = "币种类型: 1: 发行币, 2: 自发币")
    @Parameter(description = "币种类型: 1: 发行币, 2: 自发币")
    private String type;

    @Schema(title = "基础币种显示名称")
    @Parameter(description = "基础币种显示名称")
    private String bcdn;

    @Schema(title = "计价币种显示名称")
    @Parameter(description = "计价币种显示名称")
    private String qcdn;

    @Schema(title = "币图标")
    @Parameter(description = "币图标")
    private String icon;

    @Schema(title = "可交易状态 1可交易,0不可交易")
    @Parameter(description = "可交易状态 1可交易,0不可交易")
    private Long tradeState;

    @Schema(title = "交易对名称")
    @Parameter(description = "交易对名称")
    private String sn;

    @Schema(title = "最新价")
    @Parameter(description = "最新价")
    private BigDecimal price;

    @Schema(title = "开盘")
    @Parameter(description = "开盘")
    private BigDecimal open;

    @Schema(title = "收盘价")
    @Parameter(description = "收盘价")
    private BigDecimal close;

    @Schema(title = "交易量")
    @Parameter(description = "交易量")
    private BigDecimal amount;

    @Schema(title = "交易次数")
    @Parameter(description = "交易次数")
    private BigDecimal counts;

    @Schema(title = "最低")
    @Parameter(description = "最低")
    private BigDecimal low;

    @Schema(title = "最高")
    @Parameter(description = "最高")
    private BigDecimal high;

    @Schema(title = "币种计量的交易量")
    @Parameter(description = "币种计量的交易量")
    private BigDecimal vol;

    @Schema(title = "当前的最高买价")
    @Parameter(description = "当前的最高买价")
    private BigDecimal bid;

    @Schema(title = "最高买价对应的量")
    @Parameter(description = "最高买价对应的量")
    private BigDecimal bidSize;

    @Schema(title = "当前的最低卖价")
    @Parameter(description = "当前的最低卖价")
    private BigDecimal ask;

    @Schema(title = "最低卖价对应的量")
    @Parameter(description = "最低卖价对应的量")
    private BigDecimal askSize;

    @Schema(title = "最新成交价对应的量")
    @Parameter(description = "最新成交价对应的量")
    private BigDecimal lastSize;
    /**
     * 修改时间
     */
    @Schema(title = "open价格更新时间")
    private Date openTime;

}
