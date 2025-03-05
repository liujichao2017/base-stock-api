package com.hm.stock.domain.sms.sms.impl;

import com.hm.stock.domain.sms.entity.SiteSmsLog;
import com.hm.stock.domain.sms.sms.SmsClient;
import org.springframework.stereotype.Component;

@Component
public class ImitationSmsClient implements SmsClient {
    @Override
    public void init(String param) {

    }

    @Override
    public SiteSmsLog sendSms(String phone, String code, String template) {
        SiteSmsLog siteSmsLog = new SiteSmsLog();
        siteSmsLog.setPhone(phone);
        siteSmsLog.setCode(code);
        siteSmsLog.setContext(template.replace("{code}", code));
        siteSmsLog.setResBody("模拟发送");
        siteSmsLog.setStatus(1L);
        return siteSmsLog;
    }
}
