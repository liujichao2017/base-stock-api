package com.hm.stock.domain.borrow.entity;

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
* 存股借卷配置 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class BorrowCoupons extends BaseEntity {

    @Schema(title = "股票名称")
    @Parameter(description = "股票名称")
    private String stockName;

    @Schema(title = "股票代码")
    @Parameter(description = "股票代码")
    private String stockCode;

    @Schema(title = "价格")
    @Parameter(description = "价格")
    private BigDecimal price;

    @Schema(title = "最低购买额度")
    @Parameter(description = "最低购买额度")
    private BigDecimal minBuyAmt;

    @Schema(title = "总获利率")
    @Parameter(description = "总获利率")
    private BigDecimal rebate;

    @Schema(title = "周期天数")
    @Parameter(description = "周期天数")
    private Long cycle;

    @Schema(title = "市场")
    @Parameter(description = "市场")
    private Long marketId;

    @Schema(title = "开始时间")
    @Parameter(description = "开始时间")
    private Date startTime;

    @Schema(title = "结束时间")
    @Parameter(description = "结束时间")
    private Date endTime;

    @Schema(title = "需求张数")
    @Parameter(description = "需求张数")
    private Long needNumber;

    @Schema(title = "已借张数")
    @Parameter(description = "已借张数")
    private Long borrowNumber;

    @Schema(title = "股数/张")
    @Parameter(description = "股数/张")
    private Long num;

    @Schema(title = "状态：1 上架，0 下架")
    @Parameter(description = "状态：1 上架，0 下架")
    private String status;

}
