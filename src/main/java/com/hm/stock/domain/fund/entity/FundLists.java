package com.hm.stock.domain.fund.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.lang.Long;
import java.lang.String;

/**
* 基金产品 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class FundLists extends BaseEntity {

    @Schema(title = "基金名称")
    @Parameter(description = "基金名称")
    private String name;

    @Schema(title = "基金代码")
    @Parameter(description = "基金代码")
    private String code;

    @Schema(title = "类型: 1: 定投, 2: 日投日返")
    @Parameter(description = "类型: 1: 定投, 2: 日投日返")
    private String type;

    @Schema(title = "最低购买金额")
    @Parameter(description = "最低购买金额")
    private BigDecimal minAmt;

    @Schema(title = "最高购买金额")
    @Parameter(description = "最高购买金额")
    private BigDecimal maxAmt;

    @Schema(title = "开始购买时间: 09:00")
    @Parameter(description = "开始购买时间: 09:00")
    private String startBuyTime;

    @Schema(title = "购买结束时间 15:30")
    @Parameter(description = "购买结束时间 15:30")
    private String endBuyTime;

    @Schema(title = "利率(%)")
    @Parameter(description = "利率(%)")
    private BigDecimal interestRate;

    @Schema(title = "周期")
    @Parameter(description = "周期")
    private Long cycle;

    @Schema(title = "存放展示性字段")
    @Parameter(description = "存放展示性字段")
    private String extra;

    @Schema(title = "上架状态 see YNEnum, 0: 否. 1: 是 ")
    @Parameter(description = "上架状态 see YNEnum, 0: 否. 1: 是 ")
    private Long status;

}
