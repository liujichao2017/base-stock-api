package com.hm.stock.domain.news.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.news.entity.SiteNews;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface SiteNewsService extends IService<SiteNews> {
    List<SiteNews> selectList(SiteNews query);

    PageDate<SiteNews> selectByPage(SiteNews query, PageParam page);

    SiteNews detail(Long id);

    Long add(SiteNews body);

    boolean delete(Long id);

    boolean update(SiteNews body);

    Boolean sync();

}
