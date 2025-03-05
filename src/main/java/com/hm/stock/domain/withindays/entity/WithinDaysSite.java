package com.hm.stock.domain.withindays.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.lang.Long;

/**
* 日内交易配置 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class WithinDaysSite extends BaseEntity {

    @Schema(title = "市场ID")
    @Parameter(description = "市场ID")
    private Long marketId;

    @Schema(title = "最小申请金额")
    @Parameter(description = "最小申请金额")
    private BigDecimal minAmt;

    @Schema(title = "最大申请金额")
    @Parameter(description = "最大申请金额")
    private BigDecimal maxAmt;

}
