package com.hm.stock.domain.news.client.vo;

import lombok.Data;

@Data
public class JsNewsParam {
    // KEY
    private String key;
    // 域名
    private String domain;
    // 国家ID
    private String countryId;
}
