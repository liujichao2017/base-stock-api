package com.hm.stock.domain.stockdata.reset.vo;

import lombok.Data;

@Data
public class LtConfig {
    // 股票域名
    private  String stockDomain;
    // K线域名
    private  String klineDomain;
    // 国家ID
    private  String countryId;
    // 马来 认股权证 0/null 关闭, 1开启
    private String malaysiawarrants;
}
