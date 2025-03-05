package com.hm.stock.domain.coin.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.Long;
import java.lang.String;

/**
* 虚拟币交割方案 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class CoinDelivery extends BaseEntity {

    @Schema(title = "交割方案名")
    @Parameter(description = "交割方案名")
    private String name;

    @Schema(title = "时间单位: 1: 秒, 2:分, 3:小数, 4:天, 5:指定到期时间")
    @Parameter(description = "时间单位: 1: 秒, 2:分, 3:小数, 4:天, 5:指定到期时间")
    private String unit;

    @Schema(title = "时间数")
    @Parameter(description = "时间数")
    private Long time;

    @Schema(title = "启用: 1: 启用,0: 不启用")
    @Parameter(description = "启用: 1: 启用,0: 不启用")
    private Long status;

}
