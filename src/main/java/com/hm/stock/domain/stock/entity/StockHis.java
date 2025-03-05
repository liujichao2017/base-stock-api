package com.hm.stock.domain.stock.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.lang.String;

/**
* A股数据同步表 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class StockHis extends BaseEntity {

    @Schema(title = "GID")
    @Parameter(description = "GID")
    private String stockFullCode;

    @Schema(title = "最新价 last")
    @Parameter(description = "最新价 last")
    private BigDecimal zuix;

    @Schema(title = "昨收 close")
    @Parameter(description = "昨收 close")
    private BigDecimal zuos;

    @Schema(title = "涨跌幅 chg_pct")
    @Parameter(description = "涨跌幅 chg_pct")
    private BigDecimal zdf;

    @Schema(title = "总涨跌 chg")
    @Parameter(description = "总涨跌 chg")
    private BigDecimal zzd;

    @Schema(title = "今开 open")
    @Parameter(description = "今开 open")
    private BigDecimal jk;

    @Schema(title = "最高价 high")
    @Parameter(description = "最高价 high")
    private BigDecimal zg;

    @Schema(title = "最低价 low")
    @Parameter(description = "最低价 low")
    private BigDecimal zd;

    @Schema(title = "涨停")
    @Parameter(description = "涨停")
    private BigDecimal zt;

    @Schema(title = "成交量 volume")
    @Parameter(description = "成交量 volume")
    private BigDecimal cjl;

    @Schema(title = "成交额 amounts")
    @Parameter(description = "成交额 amounts")
    private BigDecimal cje;

    @Schema(title = "Gu代号")
    @Parameter(description = "Gu代号 ")
    private String stockCode;

    private String buy;


    private String sell;


    private String body;

}
