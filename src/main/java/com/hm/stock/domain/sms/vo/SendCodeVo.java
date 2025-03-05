package com.hm.stock.domain.sms.vo;

import lombok.Data;

@Data
public class SendCodeVo {
    private Long memberId;

    private String imitation;

    private String type;

    private Long smsId;

    private String phone;
}
