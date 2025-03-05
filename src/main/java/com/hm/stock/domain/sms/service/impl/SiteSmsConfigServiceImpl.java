package com.hm.stock.domain.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.member.entity.Member;
import com.hm.stock.domain.member.mapper.MemberMapper;
import com.hm.stock.domain.site.entity.DataConfig;
import com.hm.stock.domain.site.service.DataConfigService;
import com.hm.stock.domain.sms.entity.SiteSmsConfig;
import com.hm.stock.domain.sms.entity.SiteSmsLog;
import com.hm.stock.domain.sms.entity.SiteSmsTemplate;
import com.hm.stock.domain.sms.mapper.SiteSmsConfigMapper;
import com.hm.stock.domain.sms.mapper.SiteSmsLogMapper;
import com.hm.stock.domain.sms.mapper.SiteSmsTemplateMapper;
import com.hm.stock.domain.sms.service.SiteSmsConfigService;
import com.hm.stock.domain.sms.sms.SmsClient;
import com.hm.stock.domain.sms.vo.SendCodeVo;
import com.hm.stock.domain.sms.vo.SendSmsVo;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.enums.YNEnum;
import com.hm.stock.modules.execptions.CommonResultCode;
import com.hm.stock.modules.execptions.ErrorResultCode;
import com.hm.stock.modules.redis.RedisClient;
import com.hm.stock.modules.redis.RedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SiteSmsConfigServiceImpl extends ServiceImpl<SiteSmsConfigMapper, SiteSmsConfig> implements SiteSmsConfigService {
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private SiteSmsTemplateMapper siteSmsTemplateMapper;
    @Autowired
    private SiteSmsLogMapper siteSmsLogMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private DataConfigService dataConfigService;


    @Override
    public List<SiteSmsConfig> selectList(SiteSmsConfig query) {
        QueryWrapper<SiteSmsConfig> ew = new QueryWrapper<>();
        query.setStatus("1");
        ew.setEntity(query);
        ew.select("id,type,country,area_code");
        ew.orderByDesc("create_time");
        List<SiteSmsConfig> list = list(ew);
        return list;
    }

    @Override
    public PageDate<SiteSmsConfig> selectByPage(SiteSmsConfig query, PageParam page) {
        QueryWrapper<SiteSmsConfig> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<SiteSmsConfig> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public SiteSmsConfig detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(SiteSmsConfig body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(SiteSmsConfig body) {
        return updateById(body);
    }

    @Override
    public String code(SendCodeVo body) {
        Member member = null;
        Long smsId = body.getSmsId();
        if (LogicUtils.isBlank(body.getPhone())) {
            member = memberMapper.selectById(body.getMemberId());
            LogicUtils.assertNotNull(member, ErrorResultCode.E000001);

            List<SiteSmsConfig> list = list();
            for (SiteSmsConfig siteSmsConfig : list) {
                if (member.getPhone().startsWith(siteSmsConfig.getAreaCode())) {
                    smsId = siteSmsConfig.getId();
                }
            }
            body.setPhone(member.getPhone());
        }


        SendSmsVo sendSmsVo = new SendSmsVo();
        sendSmsVo.setId(smsId);
        sendSmsVo.setPhone(body.getPhone());
        sendSmsVo.setType(body.getType());
        sendSmsVo.setCheckPhone("0");
        sendSmsVo.setImitation(body.getImitation());
        send(sendSmsVo);

        return redisClient.get(RedisKey.smsCodeKey(body.getPhone()));
    }

    @Override
    public Boolean send(SendSmsVo body) {
        SiteSmsConfig siteSmsConfig = getById(body.getId());
        LogicUtils.assertNotNull(siteSmsConfig, ErrorResultCode.E000001);
        LogicUtils.assertTrue(YNEnum.no(body.getCheckPhone()) || phoneNotExists(body), ErrorResultCode.E000036);

        QueryWrapper<SiteSmsTemplate> ew = new QueryWrapper<>();
        ew.eq("type", body.getType());
        ew.last("limit 1");
        SiteSmsTemplate siteSmsTemplate = siteSmsTemplateMapper.selectOne(ew);
        LogicUtils.assertNotNull(siteSmsTemplate, ErrorResultCode.E000001);

        if (LogicUtils.isNotBlank(redisClient.get(RedisKey.smsCodeKey(body.getPhone())))) {
            return true;
        }

        String code = LogicUtils.getRandomNumber(6);

        SmsClient instance = SmsClient.getInstance(siteSmsConfig);

        // 系统配置是否实际发送短信
        instance = YNEnum.yes(getSmsEnable()) ? instance : SmsClient.getImitation();
        // 单次接口的模拟发送
        instance = YNEnum.yes(body.getImitation()) ? SmsClient.getImitation() : instance;

        SiteSmsLog siteSmsLog = instance.sendSms(body.getPhone(), code, siteSmsTemplate.getContext());

        LogicUtils.assertTrue(LogicUtils.isNotNull(siteSmsLog) && siteSmsLogMapper.insert(siteSmsLog) == 1,
                              CommonResultCode.INTERNAL_ERROR);
        redisClient.set(RedisKey.smsCodeKey(body.getPhone()), code, TimeUnit.MINUTES, getSmsTimeout());
        return siteSmsLog.getStatus() == 1L;
    }

    private String getSmsEnable() {
        DataConfig defaultValue = new DataConfig();
        defaultValue.setGroup("sms");
        defaultValue.setType("str");
        defaultValue.setKey("sms_enable");
        defaultValue.setVal("0");
        defaultValue.setRemark("短信发送开关:1:开启,0:关闭, 关闭后不实际发送短信");
        String smsEnable = dataConfigService.getByStr(defaultValue);
        return smsEnable;
    }

    private Integer getSmsTimeout() {
        DataConfig defaultValue = new DataConfig();
        defaultValue.setGroup("sms");
        defaultValue.setType("str");
        defaultValue.setKey("sms_timeout");
        defaultValue.setVal("10");
        defaultValue.setRemark("短信有效时间(分钟):整数");
        String smsEnable = dataConfigService.getByStr(defaultValue);
        return Integer.parseInt(smsEnable);
    }

    private boolean phoneNotExists(SendSmsVo body) {
        QueryWrapper<Member> ew = new QueryWrapper<>();
        ew.eq("phone", body.getPhone());
        Long count = memberMapper.selectCount(ew);
        return count == 0L;
    }

}
