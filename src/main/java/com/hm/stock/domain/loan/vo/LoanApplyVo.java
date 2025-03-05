package com.hm.stock.domain.loan.vo;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanApplyVo {

    @Schema(title = "贷款配置ID")
    @Parameter(description = "贷款配置ID")
    private Long id;


    @Schema(title = "贷款金额")
    @Parameter(description = "贷款金额")
    private BigDecimal amt;
}
