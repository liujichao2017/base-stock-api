package com.hm.stock.domain.borrow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.borrow.entity.MemberBorrowRecord;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface MemberBorrowRecordService extends IService<MemberBorrowRecord> {
    List<MemberBorrowRecord> selectList(MemberBorrowRecord query);

    PageDate<MemberBorrowRecord> selectByPage(MemberBorrowRecord query, PageParam page);

    MemberBorrowRecord detail(Long id);

    Long add(MemberBorrowRecord body);

    boolean delete(Long id);

    boolean update(MemberBorrowRecord body);
}
