package com.hm.stock.domain.member.vo;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data

public class FundConvertVo {
    @Schema(title = "转出市场ID")
    @Parameter(description = "转出市场ID")
    @NotNull
    private Long fromId;

    @Schema(title = "转入市场ID")
    @Parameter(description = "转入市场ID")
    @NotNull
    private Long toId;

    @Schema(title = "金额")
    @Parameter(description = "金额")
    @NotNull
    private BigDecimal amt;
}
