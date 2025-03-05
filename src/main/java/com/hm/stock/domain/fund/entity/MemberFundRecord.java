package com.hm.stock.domain.fund.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.lang.Long;
import java.lang.String;
import java.util.Date;

/**
* 基金购买记录 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class MemberFundRecord extends BaseEntity {

    @Schema(title = "市场ID")
    @Parameter(description = "市场ID")
    private Long marketId;

    @Schema(title = "基金ID")
    @Parameter(description = "基金ID")
    private Long fundId;

    @Schema(title = "会员Id")
    @Parameter(description = "会员Id")
    private Long memberId;

    @Schema(title = "购买金额")
    @Parameter(description = "购买金额")
    private BigDecimal amt;

    @Schema(title = "状态: 1 待审核，2 通过，3 拒绝,4 赎回")
    @Parameter(description = "状态: 1 待审核，2 通过，3 拒绝,4 赎回")
    private String status;

    @Schema(title = "通过时间")
    @Parameter(description = "通过时间")
    private Date passTime;

    @Schema(title = "赎回时间")
    @Parameter(description = "赎回时间")
    private Date sellTime;

    @TableField(exist = false)
    private BigDecimal income;
}
