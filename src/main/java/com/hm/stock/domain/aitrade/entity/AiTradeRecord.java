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
* AI交易记录表 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class AiTradeRecord extends BaseEntity {

    @Schema(title = "市场ID")
    @Parameter(description = "市场ID")
    private Long marketId;

    @Schema(title = "AI交易ID")
    @Parameter(description = "AI交易ID")
    private Long aiTradeId;

    @Schema(title = "会员Id")
    @Parameter(description = "会员Id")
    private Long memberId;

    @Schema(title = "AI交易名称")
    @Parameter(description = "AI交易名称")
    private String name;

    @Schema(title = "交易成功率(%)")
    @Parameter(description = "交易成功率(%)")
    private String tradeSucRate;

    @Schema(title = "预期收益(%)")
    @Parameter(description = "预期收益(%)")
    private String futureEarningsRate;

    @Schema(title = "交易周期(天)")
    @Parameter(description = "交易周期(天)")
    private String tradeCycle;

    @Schema(title = "买入金额")
    @Parameter(description = "买入金额")
    private BigDecimal buyAmt;

    @Schema(title = "实际收益")
    @Parameter(description = "实际收益")
    private BigDecimal incomeAmt;

    @Schema(title = "状态: 1: 申请中. 2. 申请通过(交易中) ,3.申请不通过, 4, 完成")
    @Parameter(description = "状态: 1: 申请中. 2. 申请通过(交易中) ,3.申请不通过, 4, 完成")
    private String status;

}
