package com.hm.stock.domain.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.member.entity.MemberFundsLogs;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface MemberFundsLogsService extends IService<MemberFundsLogs> {
    List<MemberFundsLogs> selectList(MemberFundsLogs query);

    PageDate<MemberFundsLogs> selectByPage(MemberFundsLogs query, PageParam page);

    MemberFundsLogs detail(Long id);

    Long add(MemberFundsLogs body);

    boolean delete(Long id);

    boolean update(MemberFundsLogs body);
}
