package com.hm.stock.domain.ipo.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.lang.Long;
import java.util.Date;
import java.lang.String;

/**
* IPO(新股)表 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class Ipo extends BaseEntity {

    @Schema(title = "新股名称")
    @Parameter(description = "新股名称")
    private String name;

    @Schema(title = "申购代码")
    @Parameter(description = "申购代码")
    private String code;

    @Schema(title = "发行价格")
    @Parameter(description = "发行价格")
    private BigDecimal price;

    @Schema(title = "绑定GID")
    @Parameter(description = "绑定GID")
    private String stockGid;

    @Schema(title = "类型：1 新股，2 配售")
    @Parameter(description = "类型：1 新股，2 配售")
    private String type;

    @Schema(title = "存放展示性字段")
    @Parameter(description = "存放展示性字段")
    private String extra;

    @Schema(title = "申购日期")
    @Parameter(description = "申购日期")
    private Date applyTime;

    @Schema(title = "公布日期")
    @Parameter(description = "公布日期")
    private Date publishTime;

    @Schema(title = "开始购买时间: 09:00")
    @Parameter(description = "开始购买时间: 09:00")
    private String startBuyTime;

    @Schema(title = "购买结束时间 15:30")
    @Parameter(description = "购买结束时间 15:30")
    private String endBuyTime;

    @Schema(title = "市场ID")
    @Parameter(description = "市场ID")
    private Long marketId;

    @Schema(title = "上架状态 see YNEnum, 0: 否. 1: 是 ")
    @Parameter(description = "上架状态 see YNEnum, 0: 否. 1: 是 ")
    private Long status;

}
