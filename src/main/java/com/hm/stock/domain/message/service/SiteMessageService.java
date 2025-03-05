package com.hm.stock.domain.message.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.message.entity.SiteMessage;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface SiteMessageService extends IService<SiteMessage> {
    List<SiteMessage> selectList(SiteMessage query);

    PageDate<SiteMessage> selectByPage(SiteMessage query, PageParam page);

    SiteMessage detail(Long id);

    Long add(SiteMessage body);

    boolean delete(Long id);

    boolean update(SiteMessage body);

    Long countUnread();

    Boolean read();
}
