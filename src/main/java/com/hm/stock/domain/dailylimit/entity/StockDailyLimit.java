package com.hm.stock.domain.dailylimit.entity;

import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.lang.Long;

/**
* 涨停表 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class StockDailyLimit extends BaseEntity {

    @Schema(title = "市场ID")
    @Parameter(description = "市场ID")
    private Long marketId;

    @Schema(title = "股票gid")
    @Parameter(description = "股票gid")
    private String stockGid;

    @Schema(title = "抢筹价格")
    @Parameter(description = "抢筹价格")
    private BigDecimal price;

    @Schema(title = "上架状态：1 是，0 否")
    @Parameter(description = "上架状态：1 是，0 否")
    private String status;

    @Schema(title = "涨跌幅")
    @Parameter(description = "涨跌幅")
    @TableField(exist = false)
    private BigDecimal last;

    @Schema(title = "涨跌幅")
    @Parameter(description = "涨跌幅")
    @TableField(exist = false)
    private BigDecimal chgPct;

    @Schema(title = "股票名称")
    @Parameter(description = "股票名称")
    @TableField(exist = false)
    private String stockName;

    @Schema(title = "股票代码")
    @Parameter(description = "股票代码")
    @TableField(exist = false)
    private String stockCode;

}
