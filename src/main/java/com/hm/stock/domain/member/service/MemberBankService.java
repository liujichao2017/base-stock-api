package com.hm.stock.domain.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.member.entity.MemberBank;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface MemberBankService extends IService<MemberBank> {
    List<MemberBank> selectList(MemberBank query);

    PageDate<MemberBank> selectByPage(MemberBank query, PageParam page);

    MemberBank detail();

    Long add(MemberBank body);

    boolean delete(Long id);

    boolean update(MemberBank body);

    MemberBank saveByMemberId(MemberBank memberBank);
}
