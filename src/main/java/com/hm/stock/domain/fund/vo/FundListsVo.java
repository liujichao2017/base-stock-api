package com.hm.stock.domain.fund.vo;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FundListsVo {
    @Schema(title = "基金产品ID")
    @Parameter(description = "基金产品ID")
    private Long id;

    @Schema(title = "市场ID")
    @Parameter(description = "市场ID")
    private Long marketId;

    @Schema(title = "购买金额")
    @Parameter(description = "购买金额")
    private BigDecimal amt;
}
