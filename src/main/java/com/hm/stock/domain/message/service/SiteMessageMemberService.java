package com.hm.stock.domain.message.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.message.entity.SiteMessageMember;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface SiteMessageMemberService extends IService<SiteMessageMember> {
    List<SiteMessageMember> selectList(SiteMessageMember query);

    PageDate<SiteMessageMember> selectByPage(SiteMessageMember query, PageParam page);

    SiteMessageMember detail(Long id);

    Long add(SiteMessageMember body);

    boolean delete(Long id);

    boolean update(SiteMessageMember body);
}
