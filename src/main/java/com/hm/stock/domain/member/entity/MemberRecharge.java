package com.hm.stock.domain.member.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.lang.Long;
import java.util.Date;
import java.lang.String;

/**
* 用户充值表 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class MemberRecharge extends BaseEntity {

    @Schema(title = "会员Id")
    @Parameter(description = "会员Id")
    private Long memberId;

    @Schema(title = "市场ID")
    @Parameter(description = "市场ID")
    private Long marketId;

    @Schema(title = "订单号")
    @Parameter(description = "订单号")
    private String orderSn;

    @Schema(title = "金额")
    @Parameter(description = "金额")
    private BigDecimal amt;

    @Schema(title = "实际金额")
    @Parameter(description = "实际金额")
    private BigDecimal actualAmt;

    @Schema(title = "手续费")
    @Parameter(description = "手续费")
    private BigDecimal fee;

    @Schema(title = "状态: 0: 待审批, 1: 通过, 2: 拒绝")
    @Parameter(description = "状态: 0: 待审批, 1: 通过, 2: 拒绝")
    private String status;

    @Schema(title = "拒绝原因")
    @Parameter(description = "拒绝原因")
    private String orderDesc;

    @Schema(title = "审批时间")
    @Parameter(description = "审批时间")
    private Date approvalTime;

    //新增字段账户类型
    @Schema(title = "账户类型")
    @Parameter(description = "账户类型，NORMAL/QUANTIFICATION")
    private String accountType;


    //新增字段货币类型
    @Schema(title = "货币类型")
    @Parameter(description = "货币类型")
    private String currencyType;
}
