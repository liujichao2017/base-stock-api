package com.hm.stock.domain.market.entity;

import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 市场 实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Market extends BaseEntity {


    @Schema(title = "名字")
    @Parameter(description = "名字")
    @TableField(condition = SqlCondition.LIKE)
    private String name;

    @Schema(title = "货币缩写")
    @Parameter(description = "货币缩写")
    private String currency;

    @Schema(title = "货币缩写")
    @Parameter(description = "货币缩写")
    private String countryImg;

    @Schema(title = "国家缩写")
    @Parameter(description = "国家缩写")
    private String country;


    @Schema(title = "市场类型: stock:股票市场,coin_usdt:U本位虚拟币")
    @Parameter(description = "市场类型: stock:股票市场,coin_usdt:U本位虚拟币")
    private String type;

    @Schema(title = "主市场 see YNEnum, 0: 否. 1: 是")
    @Parameter(description = "主市场 see YNEnum, 0: 否. 1: 是")
    private String isMaster;

    @Schema(title = "主市场对本市场货币汇率, 主市场填1")
    @Parameter(description = "主市场对本市场货币汇率, 主市场填1")
    private BigDecimal mainExchangeRate;

    @Schema(title = "从市场(本市场)对主市场货币汇率, 主市场填1")
    @Parameter(description = "从市场(本市场)对主市场货币汇率, 主市场填1")
    private BigDecimal slaveExchangeRate;

    @Schema(title = "数据源标记 see StockDataSourceEnum")
    @Parameter(description = "数据源标记 see StockDataSourceEnum")
    private String dataSourceMark;

    @Schema(title = "数据源配置JSON")
    @Parameter(description = "数据源配置JSON")
    private String dataSourceJson;

    @Schema(title = "时区")
    @Parameter(description = "时区")
    private String timeZone;

    @Schema(title = "上午开始交易时间")
    @Parameter(description = "上午开始交易时间")
    private String transAmBegin;

    @Schema(title = "上午结束交易时间")
    @Parameter(description = "上午结束交易时间")
    private String transAmEnd;

    @Schema(title = "下午开始交易时间")
    @Parameter(description = "下午开始交易时间")
    private String transPmBegin;

    @Schema(title = "下午结束交易时间")
    @Parameter(description = "下午结束交易时间")
    private String transPmEnd;

    @Schema(title = "买入手续费")
    @Parameter(description = "买入手续费")
    private BigDecimal buyFee;

    @Schema(title = "卖出手续费")
    @Parameter(description = "卖出手续费")
    private BigDecimal sellFee;

    @Schema(title = "其他手续费(买入印花税)")
    @Parameter(description = "其他手续费(买入印花税)")
    private BigDecimal otherFee;

    @Schema(title = "其他手续费(卖出印花税)")
    @Parameter(description = "其他手续费(卖出印花税)")
    private BigDecimal sellOtherFee;

    @Schema(title = "股票最小购买数量")
    @Parameter(description = "股票最小购买数量")
    private Integer minNum;

    @Schema(title = "股票最大购买数量")
    @Parameter(description = "股票最大购买数量")
    private Integer maxNum;

    @Schema(title = "最少持有时间(分钟)")
    @Parameter(description = "最少持有时间(分钟)")
    private Integer sellTime;

    @Schema(title = "涨停")
    @Parameter(description = "涨停")
    private BigDecimal chgPctLimit;

    @Schema(title = "是否支持提现")
    @Parameter(description = "是否支持提现")
    private String isWithdraw;

    @Schema(title = "是否支持充值")
    @Parameter(description = "是否支持充值")
    private String isRecharge;

    @Schema(title = "排序")
    @Parameter(description = "排序")
    private Integer sort;

    @Schema(title = "同步节假日名称")
    @Parameter(description = "同步节假日名称")
    private String syncName;
}
