package com.hm.stock.domain.news.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.news.client.NewsClient;
import com.hm.stock.domain.news.entity.SiteNews;
import com.hm.stock.domain.news.entity.SiteNewsConfig;
import com.hm.stock.domain.news.mapper.SiteNewsConfigMapper;
import com.hm.stock.domain.news.mapper.SiteNewsMapper;
import com.hm.stock.domain.news.service.SiteNewsService;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.utils.ExecuteUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SiteNewsServiceImpl extends ServiceImpl<SiteNewsMapper, SiteNews> implements SiteNewsService {
    @Autowired
    private SiteNewsConfigMapper siteSmsConfigMapper;

    @Override
    public List<SiteNews> selectList(SiteNews query) {
        QueryWrapper<SiteNews> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    public PageDate<SiteNews> selectByPage(SiteNews query, PageParam page) {
        QueryWrapper<SiteNews> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<SiteNews> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public SiteNews detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(SiteNews body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(SiteNews body) {
        return updateById(body);
    }

    @Override
    public Boolean sync() {
        QueryWrapper<SiteNewsConfig> ew = new QueryWrapper<>();
        ew.eq("status", "1");
        List<SiteNewsConfig> siteSmsConfigs = siteSmsConfigMapper.selectList(ew);
        if (LogicUtils.isEmpty(siteSmsConfigs)) {
            return false;
        }
        for (SiteNewsConfig siteSmsConfig : siteSmsConfigs) {
            ExecuteUtil.COMMON.execute(()->{
                NewsClient instance = NewsClient.getInstance(siteSmsConfig);
                instance.sync();
            });

        }
        return true;
    }
}
