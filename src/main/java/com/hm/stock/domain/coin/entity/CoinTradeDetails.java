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
public class CoinTradeDetails extends BaseEntity {

    @Schema(title = "交易对")
    @Parameter(description = "交易对")
    private String symbol;

    @Schema(title = "调整为新加坡时间的时间戳，单位毫秒")
    @Parameter(description = "调整为新加坡时间的时间戳，单位毫秒")
    private Long ts;

    @Schema(title = "交易ID")
    @Parameter(description = "交易ID")
    private Long tradeId;

    @Schema(title = "以基础币种为单位的交易量")
    @Parameter(description = "以基础币种为单位的交易量")
    private BigDecimal amount;

    @Schema(title = "以报价币种为单位的成交价格")
    @Parameter(description = "以报价币种为单位的成交价格")
    private BigDecimal price;

    @Schema(title = "交易方向：“buy” 或 “sell”, “buy” 即买，“sell” 即卖")
    @Parameter(description = "交易方向：“buy” 或 “sell”, “buy” 即买，“sell” 即卖")
    private String direction;

}
