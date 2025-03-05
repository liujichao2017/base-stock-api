package com.hm.stock.domain.aitrade.vo;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AiTradeBuyVo {


    @Schema(title = "AI产品ID")
    @Parameter(description = "AI产品ID")
    private Long id;

    @Schema(title = "申请金额")
    @Parameter(description = "申请金额")
    private BigDecimal amt;
}
