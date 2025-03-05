package com.hm.stock.domain.member.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.lang.Long;

/**
* 会员资金兑换记录表 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class MemberFundsConvert extends BaseEntity {

    @Schema(title = "会员Id")
    @Parameter(description = "会员Id")
    private Long memberId;

    @Schema(title = "转出市场")
    @Parameter(description = "转出市场")
    private Long fromId;

    @Schema(title = "转入市场")
    @Parameter(description = "转入市场")
    private Long toId;

    @Schema(title = "转出金额")
    @Parameter(description = "转出金额")
    private BigDecimal fromAmt;

    @Schema(title = "转入金额")
    @Parameter(description = "转入金额")
    private BigDecimal toAmt;

    @Schema(title = "汇率: 转出市场 : 转入市场, 韩对美0.00072, 美兑韩: 1382.28")
    @Parameter(description = "汇率: 转出市场 : 转入市场, 韩对美0.00072, 美兑韩: 1382.28")
    private BigDecimal exchangeRate;

}
