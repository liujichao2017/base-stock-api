package com.hm.stock.domain.sms.sms.vo;

import lombok.Data;

@Data
public class MacoSmsConfig {
    private String domain;
    private String key;
    private String code;
    private String secret;
}
