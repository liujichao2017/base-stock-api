package com.hm.stock.domain.coin.vo;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
public class UpdateContractVo {

    @Schema(title = "合约ID")
    @Parameter(description = "合约ID")
    @NotBlank
    private Long id;

    @Schema(title = "止盈价格")
    @Parameter(description = "止盈价格")
    private BigDecimal stopProfitAmt;

    @Schema(title = "止损价格")
    @Parameter(description = "止损价格")
    private BigDecimal stopLossAmt;
}
