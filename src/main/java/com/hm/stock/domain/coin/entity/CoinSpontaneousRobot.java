package com.hm.stock.domain.coin.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.lang.Long;

/**
*  实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class CoinSpontaneousRobot extends BaseEntity {

    @Schema(title = "交易对: 自发币ID")
    @Parameter(description = "交易对: 自发币ID")
    private Long csId;

    @Schema(title = "小于现价")
    @Parameter(description = "小于现价")
    private BigDecimal minPrice;

    @Schema(title = "大于现价")
    @Parameter(description = "大于现价")
    private BigDecimal maxPrice;

    @Schema(title = "频率:毫秒")
    @Parameter(description = "频率:毫秒")
    private Long minFrequency;

    @Schema(title = "频率:毫秒")
    @Parameter(description = "频率:毫秒")
    private Long maxFrequency;

    @Schema(title = "波幅")
    @Parameter(description = "波幅")
    private BigDecimal minAmplitude;

    @Schema(title = "波幅")
    @Parameter(description = "波幅")
    private BigDecimal maxAmplitude;

    @Schema(title = "数量")
    @Parameter(description = "数量")
    private BigDecimal minNum;

    @Schema(title = "数量")
    @Parameter(description = "数量")
    private BigDecimal maxNum;

    @Schema(title = "优先级")
    @Parameter(description = "优先级")
    private Long priority;

    @Schema(title = "状态: 0: 停用,1: 启用")
    @Parameter(description = "状态: 0: 停用,1: 启用")
    private Long status;

}
