package com.hm.stock.domain.dailylimit.vo;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DailyLimitVo {
    @Schema(title = "抢筹记录ID")
    @Parameter(description = "抢筹记录ID")
    private Long id;
    @Schema(title = "购买数量")
    @Parameter(description = "购买数量")
    private Long num;
    @Schema(title = "杠杆: 默认为1")
    @Parameter(description = "杠杆: 默认为1")
    private Long level;
}
