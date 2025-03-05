package com.hm.stock.domain.stockdata.reset.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Kline {
    //        "t": 1701302400, // 时间戳
    //        "c": "315.000", //
    //        "o": "300.000", // 开盘价
    //        "h": "315.000", // 最高价
    //        "l": "300.000", // 最低价
    //        "v": 831000, // 成交量
    //        "vo": 0
    // 时间戳
    private Long t;

    // 今收
    private BigDecimal c;

    // 今开
    private BigDecimal o;

    // 最高
    private BigDecimal h;

    // 最低
    private BigDecimal l;

    // 成交额
    private BigDecimal v;

    // 成交量
    private BigDecimal vo;
}
