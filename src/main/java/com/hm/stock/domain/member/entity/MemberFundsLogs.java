package com.hm.stock.domain.member.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.lang.Long;
import java.lang.String;

/**
* 会员资金动态表 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class MemberFundsLogs extends BaseEntity {

    @Schema(title = "会员Id")
    @Parameter(description = "会员Id")
    private Long memberId;

    @Schema(title = "市场ID")
    @Parameter(description = "市场ID")
    private Long marketId;

    @Schema(title = "操作金额")
    @Parameter(description = "操作金额")
    private BigDecimal amt;

    @Schema(title = "会员可见 see YNEnum, 0: 否(后台上下分). 1: 是(其他)")
    @Parameter(description = "会员可见 see YNEnum, 0: 否(后台上下分). 1: 是(其他)")
    private String visible;

    @Schema(title = "操作源: see FundsSourceEnum 1: 后台上下分 2: 充值, 3: 提现, 4: 货币兑换, 5之后按照业务定义")
    @Parameter(description = "操作源: see FundsSourceEnum 1: 后台上下分 2: 充值, 3: 提现, 4: 货币兑换, 5之后按照业务定义")
    private String source;

    @Schema(title = "源记录ID: 由哪条记录操作的资金")
    @Parameter(description = "源记录ID: 由哪条记录操作的资金")
    private Long sourceId;

    @Schema(title = "操作类型: see FundsOperateTypeEnum 0: 其他 1: 买入, 2: 卖出, 3: 盈亏")
    @Parameter(description = "操作类型: see FundsOperateTypeEnum 0: 其他 1: 买入, 2: 卖出, 3: 盈亏")
    private String operateType;

    @Schema(title = "可用余额,操作后台的资金变化")
    @Parameter(description = "可用余额,操作后台的资金变化")
    private BigDecimal enableAmt;

    @Schema(title = "占用金额,操作后台的资金变化")
    @Parameter(description = "占用金额,操作后台的资金变化")
    private BigDecimal occupancyAmt;

    @Schema(title = "冻结金额,操作后台的资金变化")
    @Parameter(description = "冻结金额,操作后台的资金变化")
    private BigDecimal freezeAmt;

    @Schema(title = "盈利金额,操作后台的资金变化")
    @Parameter(description = "盈利金额,操作后台的资金变化")
    private BigDecimal profitAmt;

    @Schema(title = "操作的内容,根据交易的名称,记录ID等信息拼接字符串, 用于后端展示")
    @Parameter(description = "操作的内容,根据交易的名称,记录ID等信息拼接字符串, 用于后端展示")
    private String content;

    @Schema(title = "操作的内容存放JSON,根据交易的名称,产品代码,等信息拼接JSON, 用于前端展示, 标准格式定义 name code ")
    @Parameter(description = "操作的内容存放JSON,根据交易的名称,产品代码,等信息拼接JSON, 用于前端展示, 标准格式定义 name code ")
    private String contentJson;

}
