package com.hm.stock.domain.member.vo;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateFundVo {
    @Schema(title = "用户ID")
    @Parameter(description = "用户ID")
    @NotNull
    private Long memberId;


    @Schema(title = "市场ID")
    @Parameter(description = "市场ID")
    @NotNull
    private Long marketId;

    @Schema(title = "金额")
    @Parameter(description = "金额")
    @NotNull
    private BigDecimal amt;

    @Schema(title = "类型: 1:上分, 2: 下方")
    @Parameter(description = "类型: 1:上分, 2: 下方")
    @NotBlank
    private String type;
}
