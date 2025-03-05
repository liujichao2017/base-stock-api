package com.hm.stock.domain.otc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.auth.entity.SessionInfo;
import com.hm.stock.domain.otc.entity.OtcRecord;
import com.hm.stock.domain.otc.mapper.OtcRecordMapper;
import com.hm.stock.domain.otc.service.OtcRecordService;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OtcRecordServiceImpl extends ServiceImpl<OtcRecordMapper, OtcRecord> implements OtcRecordService {
    @Override
    public List<OtcRecord> selectList(OtcRecord query) {
        QueryWrapper<OtcRecord> ew = new QueryWrapper<>();
        query.setMemberId(SessionInfo.getInstance().getId());
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    public PageDate<OtcRecord> selectByPage(OtcRecord query, PageParam page) {
        QueryWrapper<OtcRecord> ew = new QueryWrapper<>();
        query.setMemberId(SessionInfo.getInstance().getId());
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<OtcRecord> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public OtcRecord detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(OtcRecord body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(OtcRecord body) {
        return updateById(body);
    }
}
