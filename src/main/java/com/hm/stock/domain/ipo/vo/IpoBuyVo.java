package com.hm.stock.domain.ipo.vo;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class IpoBuyVo {
    @Schema(title = "新股ID")
    @Parameter(description = "新股ID")
    private Long id;
    @Schema(title = "申购数量")
    @Parameter(description = "申购数量")
    private Long num;
}
