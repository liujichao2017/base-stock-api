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
* 虚拟币委托任务 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class CoinDelegation extends BaseEntity {

    @Schema(title = "合约ID")
    @Parameter(description = "合约ID")
    private Long contractId;

    @Schema(title = "合约委托ID")
    @Parameter(description = "合约委托ID")
    private Long contractDelegationId;

    @Schema(title = "交易对")
    @Parameter(description = "交易对")
    private String symbol;

    @Schema(title = "委托价格")
    @Parameter(description = "委托价格")
    private BigDecimal price;

    @Schema(title = "交割时间: 毫秒")
    @Parameter(description = "交割时间: 毫秒")
    private Long deliveryTime;

    @Schema(title = "触发模式: 1: 现价大于等于委托价. 2: 现价小于等于委托价, 3:交割时间")
    @Parameter(description = "触发模式: 1: 现价大于等于委托价. 2: 现价小于等于委托价, 3:交割时间")
    private String triggerModel;

    @Schema(title = "操作类型:1: 买入(合约委托), 2: 卖出(合约平仓)")
    @Parameter(description = "操作类型:1: 买入(合约委托), 2: 卖出(合约平仓)")
    private String operateType;

    @Schema(title = "触发价格")
    @Parameter(description = "触发价格")
    private BigDecimal triggerPrice;

}
