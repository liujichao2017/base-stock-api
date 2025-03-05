package com.hm.stock.domain.member.vo;

import com.hm.stock.domain.experience.vo.ExperienceVo;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberFundsVo {
    @Schema(title = "会员Id")
    @Parameter(description = "会员Id")
    private Long memberId;

    @Schema(title = "市场ID")
    @Parameter(description = "市场ID")
    private Long marketId;

    @Schema(title = "货币缩写")
    @Parameter(description = "货币缩写")
    private String currency;

    @Schema(title = "可用余额")
    @Parameter(description = "可用余额")
    private BigDecimal enableAmt = BigDecimal.ZERO;

    @Schema(title = "占用金额: 购买产品后, 正在盈利的本金")
    @Parameter(description = "占用金额: 购买产品后, 正在盈利的本金")
    private BigDecimal occupancyAmt = BigDecimal.ZERO;

    @Schema(title = "冻结金额: 购买产品后, 等待审批的本金")
    @Parameter(description = "冻结金额: 购买产品后, 等待审批的本金")
    private BigDecimal freezeAmt = BigDecimal.ZERO;

    @Schema(title = "盈利金额: 购买产品结算后, 获得的收益")
    @Parameter(description = "盈利金额: 购买产品结算后, 获得的收益")
    private BigDecimal profitAmt = BigDecimal.ZERO;

    @Schema(title = "浮动盈利: 未结算的盈利")
    @Parameter(description = "浮动盈利: 未结算的盈利")
    private BigDecimal floatProfitAmt = BigDecimal.ZERO;

    @Schema(title = "体验金")
    @Parameter(description = "体验金")
    private ExperienceVo experience;
}
