package com.hm.stock.domain.sms.sms;

import com.hm.stock.domain.sms.entity.SiteSmsConfig;
import com.hm.stock.domain.sms.entity.SiteSmsLog;
import com.hm.stock.domain.sms.enums.SmsTypeEnum;
import com.hm.stock.domain.sms.sms.impl.ImitationSmsClient;
import com.hm.stock.domain.sms.sms.impl.MacoSmsClient;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.execptions.ErrorResultCode;
import com.hm.stock.modules.utils.BeanUtil;

public interface SmsClient {
    void init(String param);

    SiteSmsLog sendSms(String phone, String code, String template);

    static SmsClient getInstance(SiteSmsConfig config) {
        SmsClient client = null;
        switch (SmsTypeEnum.getEnum(config.getType())) {
            case MACO:
                client = BeanUtil.getBean(MacoSmsClient.class);
                break;
        }
        LogicUtils.assertNotNull(client, ErrorResultCode.E000001);
        client.init(config.getJson());
        return client;
    }

    static SmsClient getImitation() {
        return BeanUtil.getBean(ImitationSmsClient.class);
    }
}
