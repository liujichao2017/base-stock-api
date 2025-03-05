package com.hm.stock.domain.experience.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 体验金使用记录表 实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExperienceRecord extends BaseEntity {


    @Schema(title = "操作源操作模块")
    @Parameter(description = "操作源操作模块")
    private String source;

    @Schema(title = "操作源操作模块记录ID")
    @Parameter(description = "操作源操作模块记录ID")
    private Long sourceId;

    @Schema(title = "客户Id")
    @Parameter(description = "客户Id")
    private Long memberId;

    @Schema(title = "市场ID")
    @Parameter(description = "市场ID")
    private Long marketId;

    @Schema(title = "已使用金额")
    @Parameter(description = "已使用金额")
    private BigDecimal amt;

}
