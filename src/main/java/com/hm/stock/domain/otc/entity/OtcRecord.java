package com.hm.stock.domain.otc.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.lang.Long;
import java.lang.String;

/**
* otc(大宗)记录表 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class OtcRecord extends BaseEntity {

    @Schema(title = "市场ID")
    @Parameter(description = "市场ID")
    private Long marketId;

    @Schema(title = "会员Id")
    @Parameter(description = "会员Id")
    private Long memberId;

    @Schema(title = "股票名称")
    @Parameter(description = "股票名称")
    private String stockName;

    @Schema(title = "股票代码")
    @Parameter(description = "股票代码")
    private String stockCode;

    @Schema(title = "股票GID")
    @Parameter(description = "股票GID")
    private String stockGid;

    @Schema(title = "价格")
    @Parameter(description = "价格")
    private BigDecimal price;

    @Schema(title = "类型：备用")
    @Parameter(description = "类型：备用")
    private String type;

    @Schema(title = "申购数量")
    @Parameter(description = "申购数量")
    private Long nums;

    @Schema(title = "购买金额")
    @Parameter(description = "购买金额")
    private BigDecimal buyAmt;

    @Schema(title = "购买方向: 1, 买涨. 2. 买跌")
    @Parameter(description = "购买方向: 1, 买涨. 2. 买跌")
    private String direction;

    @Schema(title = "杠杆")
    @Parameter(description = "杠杆")
    private Long lever;

    @Schema(title = "状态: 1 待审核，2 通过，3 拒绝")
    @Parameter(description = "状态: 1 待审核，2 通过，3 拒绝")
    private String status;

    @Schema(title = "平仓时间限制: 分钟")
    @Parameter(description = "平仓时间限制: 分钟")
    private Integer sellTime;

}
