package com.hm.stock.domain.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.member.entity.MemberWithdraw;
import com.hm.stock.domain.member.vo.MemberCancelWithdrawVo;
import com.hm.stock.domain.member.vo.MemberWithdrawVo;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

public interface MemberWithdrawService extends IService<MemberWithdraw> {
    PageDate<MemberWithdraw> selectByPage(MemberWithdraw query, PageParam page);

    MemberWithdraw detail(Long id);

    Long add(MemberWithdrawVo body);

    Long userCancel(MemberCancelWithdrawVo body);
}
