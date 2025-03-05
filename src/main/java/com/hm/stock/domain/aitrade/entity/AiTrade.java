package com.hm.stock.domain.aitrade.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.lang.Long;
import java.lang.String;

/**
* AI交易表 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class AiTrade extends BaseEntity {

    @Schema(title = "市场ID")
    @Parameter(description = "市场ID")
    private Long marketId;

    @Schema(title = "AI交易名称")
    @Parameter(description = "AI交易名称")
    private String name;

    @Schema(title = "最少买入金额")
    @Parameter(description = "最少买入金额")
    private BigDecimal minBuyAmt;

    @Schema(title = "交易成功率(%)")
    @Parameter(description = "交易成功率(%)")
    private String tradeSucRate;

    @Schema(title = "预期收益(%)")
    @Parameter(description = "预期收益(%)")
    private String futureEarningsRate;

    @Schema(title = "交易周期(天)")
    @Parameter(description = "交易周期(天)")
    private String tradeCycle;

    @Schema(title = "状态: 0: 下架, 1: 上架")
    @Parameter(description = "状态: 0: 下架, 1: 上架")
    private String status;

}
