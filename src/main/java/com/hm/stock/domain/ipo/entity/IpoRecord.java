package com.hm.stock.domain.ipo.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.lang.Long;
import java.lang.String;

/**
* IPO(新股)申购记录表 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class IpoRecord extends BaseEntity {

    @Schema(title = "市场ID")
    @Parameter(description = "市场ID")
    private Long marketId;

    @Schema(title = "会员Id")
    @Parameter(description = "会员Id")
    private Long memberId;

    @Schema(title = "新股ID")
    @Parameter(description = "新股ID")
    private Long ipoId;

    @Schema(title = "类型 see IpoTypeEnum：1 新股，2 配售")
    @Parameter(description = "类型 see IpoTypeEnum：1 新股，2 配售")
    private String type;

    @Schema(title = "新股名称")
    @Parameter(description = "新股名称")
    private String name;

    @Schema(title = "申购代码")
    @Parameter(description = "申购代码")
    private String code;

    @Schema(title = "发行价格")
    @Parameter(description = "发行价格")
    private BigDecimal price;

    @Schema(title = "申购数量")
    @Parameter(description = "申购数量")
    private Long nums;

    @Schema(title = "状态: 1: 待审批, 2: 中签, 3: 未中签")
    @Parameter(description = "状态: 1: 待审批, 2: 中签, 3: 未中签")
    private String status;

    @Schema(title = "通知状态: 1: 已通知, 0: 未通知")
    @Parameter(description = "通知状态: 1: 已通知, 0: 未通知")
    private String notifyStatus;

    @Schema(title = "通知内容")
    @Parameter(description = "通知内容")
    private String notifyContext;

    @Schema(title = "中签金额")
    @Parameter(description = "中签金额")
    private BigDecimal totalAmt;

    @Schema(title = "已划转金额")
    @Parameter(description = "已划转金额")
    private BigDecimal transferAmt;

    @Schema(title = "中签数量")
    @Parameter(description = "中签数量")
    private Long winNum;
}
