package com.hm.stock.domain.loan.vo;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CountLoanVo {
    @Schema(title = "额度")
    @Parameter(description = "额度")
    private BigDecimal total;
    @Schema(title = "已借")
    @Parameter(description = "已借")
    private BigDecimal use;
    @Schema(title = "可借")
    @Parameter(description = "可借")
    private BigDecimal enable;
}
