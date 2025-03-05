package com.hm.stock.domain.sms.sms.impl;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.hm.stock.domain.sms.entity.SiteSmsLog;
import com.hm.stock.domain.sms.sms.SmsClient;
import com.hm.stock.domain.sms.sms.vo.MacoSmsConfig;
import com.hm.stock.modules.utils.HttpClient;
import com.hm.stock.modules.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class MacoSmsClient implements SmsClient {
    private static final String URL = "/sms/batch/v2?appkey=%s&appcode=%s&appsecret=%s&phone=%s&msg=%s";
    private final ThreadLocal<MacoSmsConfig> configThreadLocal = new ThreadLocal<>();

    @Override
    public void init(String param) {
        configThreadLocal.set(JsonUtil.toObj(param, MacoSmsConfig.class));
    }

    @Override
    public SiteSmsLog sendSms(String phone, String code,String template) {
        MacoSmsConfig config = configThreadLocal.get();
        String context = null;
        try {
            context = URLEncoder.encode(template.replace("{code}", code), StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            log.error("短信URLEncoder.encode失败: phone: {}, code: {}, template: {}", phone, code, template);
            return null;
        }
        String url = String.format(URL, config.getKey(), config.getCode(), config.getSecret(), phone, context);
        String content = HttpClient.sendGet(config.getDomain() + url);

        SiteSmsLog siteSmsLog = new SiteSmsLog();
        siteSmsLog.setPhone(phone);
        siteSmsLog.setCode(code);
        siteSmsLog.setContext(template.replace("{code}", code));
        siteSmsLog.setResBody(content);
        try {
            siteSmsLog.setStatus("00000".equals(JSONObject.parseObject(content).getString("code")) ? 1L : 0L);
        } catch (Exception e) {
            siteSmsLog.setStatus(0L);
        }
        return siteSmsLog;
    }
}
