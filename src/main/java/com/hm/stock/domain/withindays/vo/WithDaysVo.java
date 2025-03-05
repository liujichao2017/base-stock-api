package com.hm.stock.domain.withindays.vo;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithDaysVo {
    @Schema(title = "日内交易配置ID")
    @Parameter(description = "日内交易配置ID")
    private Long id;

    @Schema(title = "申请的日内金额")
    @Parameter(description = "申请的日内金额")
    private BigDecimal amt;
}
