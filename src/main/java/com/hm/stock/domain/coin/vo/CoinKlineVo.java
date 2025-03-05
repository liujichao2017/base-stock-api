package com.hm.stock.domain.coin.vo;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CoinKlineVo {
    @Schema(title = "交易对")
    @Parameter(description = "交易对")
    private String symbol;

    @Schema(title = "时间阶段: 1min, 5min, 15min, 30min, 60min, 1day, 1week, 1year")
    @Parameter(description = "时间阶段: 1min, 5min, 15min, 30min, 60min, 1day, 1week, 1year")
    private String period;
}
