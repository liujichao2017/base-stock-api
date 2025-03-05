package com.hm.stock.domain.coin.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.lang.Long;
import java.lang.String;

/**
*  实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class CoinKline extends BaseEntity {

    @Schema(title = "交易对")
    @Parameter(description = "交易对")
    private String symbol;

    @Schema(title = "时间阶段: 1min, 5min, 15min, 30min, 60min, 1day, 1week, 1year")
    @Parameter(description = "时间阶段: 1min, 5min, 15min, 30min, 60min, 1day, 1week, 1year")
    private String period;

    @Schema(title = "调整为新加坡时间的时间戳，单位秒，并以此作为此K线柱的id")
    @Parameter(description = "调整为新加坡时间的时间戳，单位秒，并以此作为此K线柱的id")
    private Long ts;

    @Schema(title = "交易次数")
    @Parameter(description = "交易次数")
    private Long count;

    @Schema(title = "以基础币种计量的交易量")
    @Parameter(description = "以基础币种计量的交易量")
    private BigDecimal amount;

    @Schema(title = "本阶段开盘价")
    @Parameter(description = "本阶段开盘价")
    private BigDecimal open;

    @Schema(title = "本阶段收盘价")
    @Parameter(description = "本阶段收盘价")
    private BigDecimal close;

    @Schema(title = "本阶段最低价")
    @Parameter(description = "本阶段最低价")
    private BigDecimal low;

    @Schema(title = "本阶段最高价")
    @Parameter(description = "本阶段最高价")
    private BigDecimal high;

    @Schema(title = "以报价币种计量的交易量")
    @Parameter(description = "以报价币种计量的交易量")
    private BigDecimal vol;

}
