package com.hm.stock.domain.borrow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.auth.entity.SessionInfo;
import com.hm.stock.domain.borrow.entity.MemberBorrowRecord;
import com.hm.stock.domain.borrow.mapper.MemberBorrowRecordMapper;
import com.hm.stock.domain.borrow.service.MemberBorrowRecordService;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberBorrowRecordServiceImpl extends ServiceImpl<MemberBorrowRecordMapper, MemberBorrowRecord> implements MemberBorrowRecordService {
    @Override
    public List<MemberBorrowRecord> selectList(MemberBorrowRecord query) {
        QueryWrapper<MemberBorrowRecord> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    public PageDate<MemberBorrowRecord> selectByPage(MemberBorrowRecord query, PageParam page) {
        QueryWrapper<MemberBorrowRecord> ew = new QueryWrapper<>();
        query.setMemberId(SessionInfo.getInstance().getId());
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<MemberBorrowRecord> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public MemberBorrowRecord detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(MemberBorrowRecord body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(MemberBorrowRecord body) {
        return updateById(body);
    }
}
