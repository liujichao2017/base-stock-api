package com.hm.stock.domain.stock.vo;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PositionQueryVo {
    @Schema(title = "持仓状态(必传): 1, 持仓. 2.平仓")
    @Parameter(description = "持仓状态(必传): 1, 持仓. 2.平仓")
    private String status;


}
