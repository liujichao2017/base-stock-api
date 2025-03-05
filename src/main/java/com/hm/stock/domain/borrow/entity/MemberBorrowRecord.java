package com.hm.stock.domain.borrow.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.lang.Long;
import java.util.Date;

/**
*  实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class MemberBorrowRecord extends BaseEntity {

    @Schema(title = "客户ID")
    @Parameter(description = "客户ID")
    private Long memberId;

    @Schema(title = "借劵ID")
    @Parameter(description = "借劵ID")
    private Long bcId;

    @Schema(title = "股票名称")
    @Parameter(description = "股票名称")
    private String stockName;

    @Schema(title = "股票代码")
    @Parameter(description = "股票代码")
    private String stockCode;

    @Schema(title = "股数/张")
    @Parameter(description = "股数/张")
    private Long num;

    @Schema(title = "买入张数")
    @Parameter(description = "买入张数")
    private Long buyNum;

    @Schema(title = "通过张数")
    @Parameter(description = "通过张数")
    private Long applyNum;

    @Schema(title = "买入价格")
    @Parameter(description = "买入价格")
    private BigDecimal buyPrice;

    @Schema(title = "买入总价")
    @Parameter(description = "买入总价")
    private BigDecimal buyAmt;

    @Schema(title = "买入时间")
    @Parameter(description = "买入时间")
    private Date buyTime;

    @Schema(title = "卖出时间")
    @Parameter(description = "卖出时间")
    private Date sellTime;

    @Schema(title = "周期")
    @Parameter(description = "周期")
    private Long cycle;

    @Schema(title = "周期结束时间")
    @Parameter(description = "周期结束时间")
    private Date cycleTime;

    @Schema(title = "日利率%")
    @Parameter(description = "日利率%")
    private BigDecimal rebate;

    @Schema(title = "总获利")
    @Parameter(description = "总获利")
    private BigDecimal totalIncome;

    @Schema(title = "每天应该返利")
    @Parameter(description = "每天应该返利")
    private BigDecimal dayIncome;

    @Schema(title = "到账收益")
    @Parameter(description = "到账收益")
    private BigDecimal income;

    @Schema(title = "市场")
    @Parameter(description = "市场")
    private Long marketId;

    @Schema(title = "状态:1 待审核，2 收益中， 3 拒绝， 4收益结束")
    @Parameter(description = "状态:1 待审核，2 收益中， 3 拒绝， 4收益结束")
    private Integer status;

}
