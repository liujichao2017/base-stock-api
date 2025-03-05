package com.hm.stock.domain.stock.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.Long;
import java.lang.String;

/**
* 杠杆配置 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class StockLevel extends BaseEntity {

    @Schema(title = "杠杆等级")
    @Parameter(description = "杠杆等级")
    private Long level;

    @Schema(title = "是否启用：1 是，0 否")
    @Parameter(description = "是否启用：1 是，0 否")
    private String status;

}
