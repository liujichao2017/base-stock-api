package com.hm.stock.domain.stock.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.Long;
import java.lang.String;

/**
* 节假日表 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class ClosingDateConfig extends BaseEntity {

    @Schema(title = "市场ID")
    @Parameter(description = "市场ID")
    private Long marketId;

    @Schema(title = "休市时间 2024-01-04")
    @Parameter(description = "休市时间 2024-01-04")
    private String closingDate;

}
