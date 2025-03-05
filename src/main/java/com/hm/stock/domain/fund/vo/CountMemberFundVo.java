package com.hm.stock.domain.fund.vo;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CountMemberFundVo {
    @Schema(title = "市场ID")
    @Parameter(description = "市场ID")
    private Long marketId;

    @Schema(title = "可用余额")
    @Parameter(description = "可用余额")
    private BigDecimal enableAmt;

    @Schema(title = "总收益")
    @Parameter(description = "总收益")
    private BigDecimal income;

    @Schema(title = "到账收益")
    @Parameter(description = "到账收益")
    private BigDecimal arrivalIncome;


    @Schema(title = "占用金额: 购买产品后, 正在盈利的本金")
    @Parameter(description = "占用金额: 购买产品后, 正在盈利的本金")
    private BigDecimal occupancyAmt;
}
