package com.hm.stock.domain.coin.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.lang.Long;

/**
* 虚拟币资金 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class CoinAssets extends BaseEntity {
    @Schema(title = "币类型: 1: 合约币")
    @Parameter(description = "币类型: 1: 合约币")
    private String type;

    @Schema(title = "市场ID")
    @Parameter(description = "市场ID")
    private Long marketId;

    @Schema(title = "客户ID")
    @Parameter(description = "客户ID")
    private Long memberId;

    @Schema(title = "币")
    @Parameter(description = "币")
    private String coin;

    @Schema(title = "数量")
    @Parameter(description = "数量")
    private BigDecimal num;

}
