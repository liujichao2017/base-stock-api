package com.hm.stock.domain.message.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.message.entity.MemberMessage;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface MemberMessageService extends IService<MemberMessage> {
    List<MemberMessage> selectList(MemberMessage query);

    PageDate<MemberMessage> selectByPage(MemberMessage query, PageParam page);

    MemberMessage detail(Long id);

    Long add(MemberMessage body);

    boolean delete(Long id);

    boolean update(MemberMessage body);

    Long countUnread();
    Long countUnread(Long memberId);

    Boolean read();
}
