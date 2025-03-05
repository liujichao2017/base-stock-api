package com.hm.stock.domain.member.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.lang.Long;

/**
* 会员资金表 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class MemberFunds extends BaseEntity {

    @Schema(title = "会员Id")
    @Parameter(description = "会员Id")
    private Long memberId;

    @Schema(title = "市场ID")
    @Parameter(description = "市场ID")
    private Long marketId;

    @Schema(title = "可用余额")
    @Parameter(description = "可用余额")
    private BigDecimal enableAmt;

    @Schema(title = "占用金额: 购买产品后, 正在盈利的本金")
    @Parameter(description = "占用金额: 购买产品后, 正在盈利的本金")
    private BigDecimal occupancyAmt;

    @Schema(title = "冻结金额: 购买产品后, 等待审批的本金")
    @Parameter(description = "冻结金额: 购买产品后, 等待审批的本金")
    private BigDecimal freezeAmt;

    @Schema(title = "盈利金额: 购买产品结算后, 获得的收益")
    @Parameter(description = "盈利金额: 购买产品结算后, 获得的收益")
    private BigDecimal profitAmt;

}
