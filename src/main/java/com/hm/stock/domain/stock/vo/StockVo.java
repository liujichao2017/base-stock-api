package com.hm.stock.domain.stock.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class StockVo {
    @Schema(title = "关键字: CODE,NAME模糊搜索")
    @Parameter(description = "关键字: CODE,NAME模糊搜索")
    private String keywords;

    @Schema(title = "股票代码")
    @Parameter(description = "股票代码")
    private String code;

    @Schema(title = "股票名称")
    @Parameter(description = "股票名称")
    private String name;

    @Schema(title = "类型 1: 股票. 2: 指数")
    @Parameter(description = "类型 1: 股票. 2: 指数")
    private String type;


    @Schema(title = "热门  see YNEnum, 0: 否. 1: 是")
    @Parameter(description = "热门  see YNEnum, 0: 否. 1: 是")
    private String isPopular;

    @Schema(title = "市场ID")
    @Parameter(description = "市场ID")
    private Long marketId;

    @Schema(title = "K线")
    @Parameter(description = "K线")
    @TableField(exist = false)
    private String kline;
}
