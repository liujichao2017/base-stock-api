package com.hm.stock.domain.stock.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class MemberPositionVo {
    @Schema(title = "ID")
    private Long id;

    /**
     * 创建时间 自动生成
     */
    @Schema(title = "创建时间")
    private Date createTime;

    /**
     * 修改时间
     */
    @Schema(title = "修改时间")
    private Date updateTime;

    @Schema(title = "持仓号")
    @Parameter(description = "持仓号")
    private String positionSn;

    @Schema(title = "会员Id")
    @Parameter(description = "会员Id")
    private Long memberId;

    @Schema(title = "市场ID")
    @Parameter(description = "市场ID")
    private Long marketId;

    @Schema(title = "货币缩写")
    @Parameter(description = "货币缩写")
    private String currency;

    @Schema(title = "股票名称")
    @Parameter(description = "股票名称")
    private String stockName;

    @Schema(title = "股票代码")
    @Parameter(description = "股票代码")
    private String stockCode;

    @Schema(title = "股票GID")
    @Parameter(description = "股票GID")
    private String stockGid;

    @Schema(title = "购买时间")
    @Parameter(description = "购买时间")
    private Date buyOrderTime;

    @Schema(title = "买入价格")
    @Parameter(description = "买入价格")
    private BigDecimal buyOrderPrice;

    @Schema(title = "卖出时间")
    @Parameter(description = "卖出时间")
    private Date sellOrderTime;

    @Schema(title = "卖出价格/持仓时是现价")
    @Parameter(description = "卖出价格/持仓时是现价")
    private BigDecimal sellOrderPrice;

    @Schema(title = "购买方向: 1, 买涨. 2. 买跌")
    @Parameter(description = "购买方向: 1, 买涨. 2. 买跌")
    private String direction;

    @Schema(title = "买入数量")
    @Parameter(description = "买入数量")
    private Long num;

    @Schema(title = "杠杆")
    @Parameter(description = "杠杆")
    private Long lever;

    @Schema(title = "总金额")
    @Parameter(description = "总金额")
    private BigDecimal totalAmt;

    @Schema(title = "本金")
    @Parameter(description = "本金")
    private BigDecimal principalAmt;

    @Schema(title = "买入手续费")
    @Parameter(description = "买入手续费")
    private BigDecimal buyFee;

    @Schema(title = "卖出手续费")
    @Parameter(description = "卖出手续费")
    private BigDecimal sellFee;

    @Schema(title = "其他手续费")
    @Parameter(description = "其他手续费")
    private BigDecimal otherFee;

    @Schema(title = "盈亏: 扣除手续费前")
    @Parameter(description = "盈亏: 扣除手续费前")
    private BigDecimal profitAndLose;

    @Schema(title = "总盈亏: 扣除手续费后")
    @Parameter(description = "总盈亏: 扣除手续费后")
    private BigDecimal allProfitAndLose;

    @Schema(title = "是否锁仓 see YNEnum, 0: 否. 1: 是 ")
    @Parameter(description = "是否锁仓 see YNEnum, 0: 否. 1: 是 ")
    private String isLock;

    @Schema(title = "锁仓原因")
    @Parameter(description = "锁仓原因")
    private String lockMsg;

    @Schema(title = "从那个模块生成的持仓单 see FundsSourceEnum 与资金类型共用枚举")
    @Parameter(description = "从那个模块生成的持仓单 see FundsSourceEnum 与资金类型共用枚举")
    private String source;

    @Schema(title = "生成持仓单的ID")
    @Parameter(description = "生成持仓单的ID")
    private Long sourceId;
}
