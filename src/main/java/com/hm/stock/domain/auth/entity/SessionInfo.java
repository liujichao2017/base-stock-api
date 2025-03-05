package com.hm.stock.domain.auth.entity;

import com.hm.stock.domain.auth.service.AuthService;
import com.hm.stock.domain.member.entity.Member;
import com.hm.stock.domain.member.mapper.MemberMapper;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.execptions.CommonResultCode;
import com.hm.stock.modules.utils.BeanUtil;
import lombok.Data;

@Data
public class SessionInfo {


    /**
     * 会员id
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;


    public static SessionInfo getInstance() {
        AuthService authService = BeanUtil.getBean(AuthService.class);
        LogicUtils.assertNotNull(authService, CommonResultCode.LOGIN_ERROR);
        return authService.getCurrentSession();
    }

    public static Member getMember() {
        AuthService authService = BeanUtil.getBean(AuthService.class);
        LogicUtils.assertNotNull(authService, CommonResultCode.LOGIN_ERROR);
        SessionInfo currentSession = authService.getCurrentSession();
        MemberMapper memberMapper = BeanUtil.getBean(MemberMapper.class);
        Member member = memberMapper.selectById(currentSession.getId());
        LogicUtils.assertNotNull(member, CommonResultCode.LOGIN_ERROR);
        return member;
    }

    public static SessionInfo parse(String token) {
        AuthService authService = BeanUtil.getBean(AuthService.class);
        LogicUtils.assertNotNull(authService, CommonResultCode.LOGIN_ERROR);
        return authService.getCurrentSession(token);
    }
}
