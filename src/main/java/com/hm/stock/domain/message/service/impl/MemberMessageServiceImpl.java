package com.hm.stock.domain.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.auth.entity.SessionInfo;
import com.hm.stock.domain.ipo.entity.IpoRecord;
import com.hm.stock.domain.ipo.mapper.IpoRecordMapper;
import com.hm.stock.domain.message.entity.MemberMessage;
import com.hm.stock.domain.message.mapper.MemberMessageMapper;
import com.hm.stock.domain.message.service.MemberMessageService;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.utils.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberMessageServiceImpl extends ServiceImpl<MemberMessageMapper, MemberMessage> implements MemberMessageService {

    @Autowired
    private MemberMessageMapper memberMessageMapper;

    @Autowired
    private IpoRecordMapper ipoRecordMapper;

    @Override
    public List<MemberMessage> selectList(MemberMessage query) {
        QueryWrapper<MemberMessage> ew = new QueryWrapper<>();
        // 前台查询ipo的中签信息，在中签日才通知
        if (null != query.getSource()) {
            ew.lt("push_time", DateTimeUtil.getDate());
            ew.eq("read_status", 1);
        }
        query.setMemberId(SessionInfo.getInstance().getId());
        ew.setEntity(query);
        ew.orderByDesc("create_time");

        List<MemberMessage> list = list(ew);
        list.forEach(memberMessage -> {
            if (null != memberMessage.getProductId()) {
                IpoRecord ipoRecord = ipoRecordMapper.selectById(memberMessage.getProductId());
                if (null != ipoRecord && null != ipoRecord.getMarketId()) {
                    // 返回市场id,前台用于跳转申购列表
                    memberMessage.setMarketId(ipoRecord.getMarketId());
                }
                // 更新为已读
                MemberMessage update = new MemberMessage();
                update.setReadStatus(2L);
                update.setId(memberMessage.getId());
                memberMessageMapper.updateById(update);
            }
        });
        return list;
    }

    @Override
    public PageDate<MemberMessage> selectByPage(MemberMessage query, PageParam page) {
        QueryWrapper<MemberMessage> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<MemberMessage> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public MemberMessage detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(MemberMessage body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(MemberMessage body) {
        return updateById(body);
    }

    @Override
    public Long countUnread() {
        return countUnread(SessionInfo.getInstance().getId());
    }

    @Override
    public Long countUnread(Long memberId) {
        QueryWrapper<MemberMessage> ew = new QueryWrapper<>();
        ew.eq("member_id", memberId);
        ew.eq("read_status", "1");
        return count(ew);
    }

    @Override
    public Boolean read() {
        UpdateWrapper<MemberMessage> uw = new UpdateWrapper<>();
        uw.set("read_status", "2");
        uw.eq("member_id", SessionInfo.getInstance().getId());
        return update(uw);
    }
}
