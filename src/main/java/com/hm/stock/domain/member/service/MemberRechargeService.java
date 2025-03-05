package com.hm.stock.domain.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.member.entity.MemberRecharge;
import com.hm.stock.domain.member.vo.MemberRechargeVo;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface MemberRechargeService extends IService<MemberRecharge> {

    PageDate<MemberRecharge> selectByPage(MemberRecharge query, PageParam page);


    Long add(MemberRechargeVo body);


}
