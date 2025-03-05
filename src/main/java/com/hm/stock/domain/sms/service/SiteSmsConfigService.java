package com.hm.stock.domain.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.sms.entity.SiteSmsConfig;
import com.hm.stock.domain.sms.vo.SendCodeVo;
import com.hm.stock.domain.sms.vo.SendSmsVo;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface SiteSmsConfigService extends IService<SiteSmsConfig> {
    List<SiteSmsConfig> selectList(SiteSmsConfig query);

    PageDate<SiteSmsConfig> selectByPage(SiteSmsConfig query, PageParam page);

    SiteSmsConfig detail(Long id);

    Long add(SiteSmsConfig body);

    boolean delete(Long id);

    boolean update(SiteSmsConfig body);

    Boolean send(SendSmsVo body);

    String code(SendCodeVo body);
}
