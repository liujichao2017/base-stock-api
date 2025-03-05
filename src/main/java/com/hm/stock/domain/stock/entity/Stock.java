package com.hm.stock.domain.stock.entity;

import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.lang.Long;
import java.lang.String;

/**
* 股票 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class Stock extends BaseEntity {

    @Schema(title = "股票名称")
    @Parameter(description = "股票名称")
    @TableField(condition = SqlCondition.LIKE)
    private String name;

    @Schema(title = "股票代码")
    @Parameter(description = "股票代码")
    private String code;

    @Schema(title = "交易所")
    @Parameter(description = "交易所")
    private String exchanges;

    @Schema(title = "股票GID, 数据源中的ID")
    @Parameter(description = "股票GID, 数据源中的ID")
    private String gid;

    @Schema(title = "类型 1: 股票. 2: 指数")
    @Parameter(description = "类型 1: 股票. 2: 指数")
    private String type;

    @Schema(title = "市场ID")
    @Parameter(description = "市场ID")
    private Long marketId;

    @Schema(title = "禁止交易  see YNEnum, 0: 否. 1: 是")
    @Parameter(description = "禁止交易  see YNEnum, 0: 否. 1: 是")
    private String isLock;

    @Schema(title = "显示  see YNEnum, 0: 否. 1: 是")
    @Parameter(description = "显示  see YNEnum, 0: 否. 1: 是")
    private String isShow;

    @Schema(title = "热门  see YNEnum, 0: 否. 1: 是")
    @Parameter(description = "热门  see YNEnum, 0: 否. 1: 是")
    private String isPopular;

    @Schema(title = "最新价")
    @Parameter(description = "最新价")
    private BigDecimal last;

    @Schema(title = "涨跌额")
    @Parameter(description = "涨跌额")
    private BigDecimal chg;

    @Schema(title = "涨跌幅")
    @Parameter(description = "涨跌幅")
    private BigDecimal chgPct;

    @Schema(title = "最高")
    @Parameter(description = "最高")
    private BigDecimal high;

    @Schema(title = "最低")
    @Parameter(description = "最低")
    private BigDecimal low;

    @Schema(title = "今开")
    @Parameter(description = "今开")
    private BigDecimal open;

    @Schema(title = "昨收")
    @Parameter(description = "昨收")
    private BigDecimal close;

    @Schema(title = "成交量")
    @Parameter(description = "成交量")
    private BigDecimal volume;

    @Schema(title = "成交额")
    @Parameter(description = "成交额")
    private BigDecimal amounts;


    @Schema(title = "K线")
    @Parameter(description = "K线")
    @TableField(exist = false)
    private String kline;

}
