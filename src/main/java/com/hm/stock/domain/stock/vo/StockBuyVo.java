package com.hm.stock.domain.stock.vo;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class StockBuyVo {
    @Schema(title = "股票GID")
    @Parameter(description = "股票GID")
    private String stockGid;

    @Schema(title = "购买方向: 1, 买涨. 2. 买跌")
    @Parameter(description = "购买方向: 1, 买涨. 2. 买跌")
    private String direction;

    @Schema(title = "买入数量")
    @Parameter(description = "买入数量")
    private Long num;

    @Schema(title = "杠杆")
    @Parameter(description = "杠杆")
    private Long lever;





}
