package com.hm.stock.domain.stock.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.Long;
import java.lang.String;

/**
* 股票自选 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class StockOption extends BaseEntity {

    @Schema(title = "会员Id")
    @Parameter(description = "会员Id")
    private Long memberId;

    @Schema(title = "股票GID")
    @Parameter(description = "股票GID")
    private String stockGid;

    @Schema(title = "类型 1: 股票. 2: 指数")
    @Parameter(description = "类型 1: 股票. 2: 指数")
    private String type;

}
