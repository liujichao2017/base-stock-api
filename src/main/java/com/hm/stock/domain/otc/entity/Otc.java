package com.hm.stock.domain.otc.entity;

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
* otc(大宗)表 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class Otc extends BaseEntity {

    @Schema(title = "市场ID")
    @Parameter(description = "市场ID")
    private Long marketId;

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

    @Schema(title = "交易密码")
    @Parameter(description = "交易密码")
    private String password;

    @Schema(title = "开始购买时间: 09:00")
    @Parameter(description = "开始购买时间: 09:00")
    private String startBuyTime;

    @Schema(title = "购买结束时间 15:30")
    @Parameter(description = "购买结束时间 15:30")
    private String endBuyTime;

    @Schema(title = "上架状态 see YNEnum, 0: 否. 1: 是 ")
    @Parameter(description = "上架状态 see YNEnum, 0: 否. 1: 是 ")
    private Long status;

    @Schema(title = "存放展示性字段")
    @Parameter(description = "存放展示性字段")
    private String extra;

    @Schema(title = "平仓时间限制: 分钟")
    @Parameter(description = "平仓时间限制: 分钟")
    private Integer sellTime;


    @TableField(exist = false)
    private BigDecimal last;


    @TableField(exist = false)
    private BigDecimal chgPct;
}
