package com.hm.stock.domain.withindays.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.auth.entity.SessionInfo;
import com.hm.stock.domain.loan.vo.LoanApplyVo;
import com.hm.stock.domain.loan.vo.LoanConfigVo;
import com.hm.stock.domain.market.entity.Market;
import com.hm.stock.domain.member.entity.Member;
import com.hm.stock.domain.withindays.entity.WithinDaysRecord;
import com.hm.stock.domain.withindays.mapper.WithinDaysRecordMapper;
import com.hm.stock.domain.withindays.service.WithinDaysRecordService;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.execptions.ErrorResultCode;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WithinDaysRecordServiceImpl extends ServiceImpl<WithinDaysRecordMapper, WithinDaysRecord> implements WithinDaysRecordService {
    @Override
    public List<WithinDaysRecord> selectList(WithinDaysRecord query) {
        QueryWrapper<WithinDaysRecord> ew = new QueryWrapper<>();
        query.setMemberId(SessionInfo.getInstance().getId());
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    public PageDate<WithinDaysRecord> selectByPage(WithinDaysRecord query, PageParam page) {
        QueryWrapper<WithinDaysRecord> ew = new QueryWrapper<>();
        query.setMemberId(SessionInfo.getInstance().getId());
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<WithinDaysRecord> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public WithinDaysRecord detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(WithinDaysRecord body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(WithinDaysRecord body) {
        return updateById(body);
    }


}
