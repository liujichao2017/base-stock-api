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
* 用户提现表 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class MemberWithdraw extends BaseEntity {

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

    @Schema(title = "状态: 0: 待审批, 1: 通过, 2: 拒绝 3: 用户取消")
    @Parameter(description = "状态: 0: 待审批, 1: 通过, 2: 拒绝 3: 用户取消")
    private String status;

    @Schema(title = "拒绝原因")
    @Parameter(description = "拒绝原因")
    private String orderDesc;

    @Schema(title = "审批时间")
    @Parameter(description = "审批时间")
    private Date approvalTime;

    @Schema(title = "预留字段,根据项目需要使用: 银行名称")
    @Parameter(description = "预留字段,根据项目需要使用: 银行名称")
    private String bank1;

    @Schema(title = "预留字段,根据项目需要使用: 银行卡号")
    @Parameter(description = "预留字段,根据项目需要使用: 银行卡号")
    private String bank2;

    @Schema(title = "预留字段,根据项目需要使用: 银行代码")
    @Parameter(description = "预留字段,根据项目需要使用: 银行代码")
    private String bank3;

    @Schema(title = "预留字段,根据项目需要使用: 开户人名称")
    @Parameter(description = "预留字段,根据项目需要使用: 开户人名称")
    private String bank4;

    @Schema(title = "预留字段,根据项目需要使用")
    @Parameter(description = "预留字段,根据项目需要使用")
    private String bank5;

    @Schema(title = "预留字段,根据项目需要使用")
    @Parameter(description = "预留字段,根据项目需要使用")
    private String bank6;

    @Schema(title = "预留字段,根据项目需要使用")
    @Parameter(description = "预留字段,根据项目需要使用")
    private String bank7;

    @Schema(title = "预留字段,根据项目需要使用")
    @Parameter(description = "预留字段,根据项目需要使用")
    private String bank8;

    @Schema(title = "货币类型")
    @Parameter(description = "货币类型")
    private String currencyType;

    @Schema(title = "账号类型")
    @Parameter(description = "账号类型")
    private String accountType;

}
