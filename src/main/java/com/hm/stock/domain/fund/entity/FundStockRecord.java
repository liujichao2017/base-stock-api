package com.hm.stock.domain.fund.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.Long;
import java.lang.String;

/**
* 基金股票子项 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class FundStockRecord extends BaseEntity {

    @Schema(title = "基金ID")
    @Parameter(description = "基金ID")
    private Long fundId;

    @Schema(title = "关联的code")
    @Parameter(description = "关联的code")
    private String code;

    @Schema(title = "管理股票类型: hk(牛熊股),ml(结构认权股)")
    @Parameter(description = "管理股票类型: hk(牛熊股),ml(结构认权股)")
    private String type;

    @Schema(title = "是否显示 see YNEnum, 0: 否. 1: 是 ")
    @Parameter(description = "是否显示 see YNEnum, 0: 否. 1: 是 ")
    private String status;

    @TableField(exist = false)
    @Schema(title = "基金名称")
    @Parameter(description = "基金名称")
    private String fundName;

    @TableField(exist = false)
    @Schema(title = "基金代码")
    @Parameter(description = "基金代码")
    private String fundCode;

    @TableField(exist = false)
    @Schema(title = "股票名称")
    @Parameter(description = "股票名称")
    private String stockName;

    @TableField(exist = false)
    @Schema(title = "股票代码")
    @Parameter(description = "股票代码")
    private String stockCode;

    @TableField(exist = false)
    @Schema(title = "股票类型: 熊市 P /牛市 C")
    @Parameter(description = "")
    private String stockType;

}
