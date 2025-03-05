package com.hm.stock.domain.message.service.impl;
import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.auth.entity.SessionInfo;
import com.hm.stock.domain.message.entity.SiteMessage;
import com.hm.stock.domain.message.entity.SiteMessageMember;
import com.hm.stock.domain.message.mapper.SiteMessageMapper;
import com.hm.stock.domain.message.mapper.SiteMessageMemberMapper;
import com.hm.stock.domain.message.service.SiteMessageService;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SiteMessageServiceImpl extends ServiceImpl<SiteMessageMapper, SiteMessage> implements SiteMessageService {
    @Autowired
    private SiteMessageMapper siteMessageMapper;
    @Autowired
    private SiteMessageMemberMapper siteMessageMemberMapper;


    @Override
    public List<SiteMessage> selectList(SiteMessage query) {
        QueryWrapper<SiteMessage> ew = new QueryWrapper<>();
        query.setStatus(1L);
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        List<SiteMessage> list = list(ew);
        for (SiteMessage siteMessage : list) {
            QueryWrapper<SiteMessageMember> ew1 = new QueryWrapper<>();
            ew1.eq("member_id", SessionInfo.getInstance().getId());
            ew1.eq("message_id", siteMessage.getId());
            Long count = siteMessageMemberMapper.selectCount(ew1);
            siteMessage.setReadStatus(count != null && count > 0L ? "1" : "0");
        }
        if (LogicUtils.isNotBlank(query.getReadStatus())) {
            return list.stream().filter(s -> query.getReadStatus().equals(s.getReadStatus())).collect(Collectors.toList());
        }

        return list;
    }

    @Override
    public PageDate<SiteMessage> selectByPage(SiteMessage query, PageParam page) {
        QueryWrapper<SiteMessage> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<SiteMessage> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public SiteMessage detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(SiteMessage body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(SiteMessage body) {
        return updateById(body);
    }

    @Override
    public Long countUnread() {
        SiteMessage query = new SiteMessage();
        query.setReadStatus("0");

        return Long.valueOf(selectList(query).size());
    }

    @Override
    public Boolean read() {
        SiteMessage query = new SiteMessage();
        query.setReadStatus("0");
        List<SiteMessage> siteMessages = selectList(query);
        Long memberId = SessionInfo.getInstance().getId();
        for (SiteMessage siteMessage : siteMessages) {
            SiteMessageMember siteMessageMember = new SiteMessageMember();
            siteMessageMember.setMemberId(memberId);
            siteMessageMember.setMessageId(siteMessage.getId());
            siteMessageMemberMapper.insert(siteMessageMember);
        }
        return true;
    }
}
