package com.hm.stock.domain.fund.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.lang.Long;

/**
* 基金盈利表 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class FundInterestRate extends BaseEntity {

    @Schema(title = "基金ID")
    @Parameter(description = "基金ID")
    private Long fundId;

    @Schema(title = "日利率")
    @Parameter(description = "日利率")
    private BigDecimal interestRate;

    @Schema(title = "周期")
    @Parameter(description = "周期")
    private Long cycle;

}
