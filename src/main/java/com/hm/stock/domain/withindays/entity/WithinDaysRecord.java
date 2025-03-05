package com.hm.stock.domain.withindays.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.lang.Long;

/**
* 用户日内交易记录 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class WithinDaysRecord extends BaseEntity {

    @Schema(title = "市场ID")
    @Parameter(description = "市场ID")
    private Long marketId;

    @Schema(title = "会员Id")
    @Parameter(description = "会员Id")
    private Long memberId;

    @Schema(title = "申请金额")
    @Parameter(description = "申请金额")
    private BigDecimal amt;

    @Schema(title = "状态：1 待审核, 2 待建仓(同意), 3 完成 4 拒绝")
    @Parameter(description = "状态：1 待审核, 2 待建仓(同意), 3 完成 4 拒绝")
    private Long status;

}
