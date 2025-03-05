package com.hm.stock.domain.experience.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 体验金表 实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Experience extends BaseEntity {

    @Schema(title = "客户ID")
    @Parameter(description = "客户ID")
    private Long memberId;

    @Schema(title = "市场ID")
    @Parameter(description = "市场ID")
    private Long marketId;

    @Schema(title = "体验金")
    @Parameter(description = "体验金")
    private BigDecimal amt;

    @Schema(title = "已使用金额")
    @Parameter(description = "已使用金额")
    private BigDecimal useAmt;

    @Schema(title = "使用次数")
    @Parameter(description = "使用次数")
    private Long count;

    @Schema(title = "已使用次数")
    @Parameter(description = "已使用次数")
    private Long useCount;

    @Schema(title = "截止时间")
    @Parameter(description = "截止时间")
    private Date deadline;

    @Schema(title = "需要充值")
    @Parameter(description = "需要充值")
    private BigDecimal needRechargeAmt;

    @Schema(title = "已充值")
    @Parameter(description = "已充值")
    private BigDecimal rechargeAmt;

    @Schema(title = "锁定状态: 1:锁定,0:未锁定")
    @Parameter(description = "锁定状态: 1:锁定,0:未锁定")
    private String lockStatus;

    @Schema(title = "状态: 1: 可使用,2: 锁定,3,已使用 4,失效")
    @Parameter(description = "状态: 1: 可使用,2: 锁定,3,已使用 4,失效")
    @TableField(exist = false)
    private Integer status;

}
