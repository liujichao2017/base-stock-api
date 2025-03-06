package com.hm.stock.domain.member.vo;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberRechargeVo {

    @Schema(title = "市场ID")
    @Parameter(description = "市场ID")
    private Long marketId;

    @Schema(title = "金额")
    @Parameter(description = "金额")
    private BigDecimal amt;

    //增加了两个参数，货币类型和账户类型
    @Schema(title = "货币类型")
    @Parameter(description = "货币类型")
    private String currencyType;

    @Schema(title = "账号类型")
    @Parameter(description = "账号类型")
    private String accountType;
}
