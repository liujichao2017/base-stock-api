package com.hm.stock.domain.stock.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.String;

/**
* 港股-熊牛市股 实体类
*/
@Data
public class StockHkBearbull {

    @Schema(title = "股票代码")
    @Parameter(description = "股票代码")
    @TableId
    private String sym;

    @Schema(title = "股票名称")
    @Parameter(description = "股票名称")
    private String desp;

    @Schema(title = "热门")
    @Parameter(description = "热门")
    private String rank;

    @Schema(title = "熊市 P /牛市 C")
    @Parameter(description = "熊市 P /牛市 C")
    private String type;

    @Schema(title = "相关资产")
    @Parameter(description = "相关资产")
    private String udly;

    @Schema(title = "发行")
    @Parameter(description = "发行")
    private String issuer;

    @Schema(title = "现价")
    @Parameter(description = "现价")
    private String last;

    @Schema(title = "跌涨额")
    @Parameter(description = "跌涨额")
    private String chg;

    @Schema(title = "跌涨幅")
    @Parameter(description = "跌涨幅")
    private String pctchg;

    @Schema(title = "成交额")
    @Parameter(description = "成交额")
    private String turn;

    @Schema(title = "溢价")
    @Parameter(description = "溢价")
    private String premi;

    @Schema(title = "行使价")
    @Parameter(description = "行使价")
    private String strike;

    @Schema(title = "回收价")
    @Parameter(description = "回收价")
    private String calllv;

    @Schema(title = "杠杆")
    @Parameter(description = "杠杆")
    private String gear;

    @Schema(title = "换股比率")
    @Parameter(description = "换股比率")
    private String enratio;

    @Schema(title = "街货(%)")
    @Parameter(description = "街货(%)")
    private String pctout;

    @Schema(title = "街货量")
    @Parameter(description = "街货量")
    private String outq;

    @Schema(title = "最后交易日")
    @Parameter(description = "最后交易日")
    private String ldate;

    @Schema(title = "sign")
    @Parameter(description = "sign")
    private String sign;

    @Schema(title = "mostatus")
    @Parameter(description = "mostatus")
    private String mostatus;

    @Schema(title = "HistoricalHigh 历史最高")
    @Parameter(description = "HistoricalHigh 历史最高")
    private String highlow;

    @Schema(title = "价内/外")
    @Parameter(description = "价内/外")
    private String movalue;

    @Schema(title = "每手股数")
    @Parameter(description = "每手股数")
    private String lots;

    @Schema(title = "距回收价")
    @Parameter(description = "距回收价")
    private String spcall;

    @Schema(title = "距回收价(%)")
    @Parameter(description = "距回收价(%)")
    private String spcallpct;

    @Schema(title = "剩余交易日")
    @Parameter(description = "剩余交易日")
    private String day;

    @Schema(title = "搜索")
    @Parameter(description = "搜索")
    @TableField(exist = false)
    private String code;
}
