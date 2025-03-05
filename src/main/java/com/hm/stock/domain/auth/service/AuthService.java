package com.hm.stock.domain.auth.service;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hm.stock.domain.auth.entity.*;
import com.hm.stock.domain.member.entity.Member;
import com.hm.stock.domain.member.mapper.MemberMapper;
import com.hm.stock.domain.member.service.MemberService;
import com.hm.stock.domain.site.entity.SysUser;
import com.hm.stock.domain.site.mapper.SysUserMapper;
import com.hm.stock.domain.sms.entity.SiteSmsConfig;
import com.hm.stock.domain.sms.mapper.SiteSmsConfigMapper;
import com.hm.stock.modules.common.Constant;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.enums.YNEnum;
import com.hm.stock.modules.execptions.CommonResultCode;
import com.hm.stock.modules.execptions.ErrorResultCode;
import com.hm.stock.modules.execptions.InternalException;
import com.hm.stock.modules.redis.RedisClient;
import com.hm.stock.modules.redis.RedisKey;
import com.hm.stock.modules.utils.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {
    @Autowired
    private MemberService memberService;

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SiteSmsConfigMapper siteSmsConfigMapper;


    public LoginResult login(LoginInfo loginInfo) {
        QueryWrapper<Member> ew = new QueryWrapper<>();
        ew.eq("phone", loginInfo.getUsername());
        ew.eq("password", loginInfo.getPassword());
        ew.last("limit 1");
        Member member = memberService.getOne(ew);
        LogicUtils.assertNotNull(member, ErrorResultCode.E000002);
        LogicUtils.assertTrue(YNEnum.yes(member.getIsLogin()), ErrorResultCode.E000007);
        SessionInfo sessionInfo = new SessionInfo();
        sessionInfo.setId(member.getId());
        sessionInfo.setUserId(member.getUserId());

        LogicUtils.assertNotNull(sessionInfo, ErrorResultCode.E000002);
        LoginResult loginResult = new LoginResult();
        loginResult.setToken(UUID.randomUUID().toString().replaceAll("-", "").toUpperCase(Locale.ENGLISH));
        loginResult.setId(sessionInfo.getId());
        redisClient.set(RedisKey.tokenKey(loginResult.getToken()), sessionInfo, TimeUnit.DAYS, 3);
        redisClient.set(RedisKey.tokenByIdKey(sessionInfo.getId()), loginResult.getToken(), TimeUnit.DAYS, 3);
        return loginResult;
    }

    public SessionInfo getCurrentSession() {
        String token = HttpUtil.getHeader(Constant.TOKEN_HEADER_KEY);
        LogicUtils.assertNotBlank(token, CommonResultCode.LOGIN_ERROR);

        SessionInfo sessionInfo = redisClient.get(RedisKey.tokenKey(token), SessionInfo.class);
        LogicUtils.assertNotNull(sessionInfo, CommonResultCode.LOGIN_ERROR);
        LogicUtils.assertNotNull(sessionInfo.getId(), CommonResultCode.LOGIN_ERROR);
        return sessionInfo;
    }

    public SessionInfo getCurrentSession(String token) {
        LogicUtils.assertNotBlank(token, CommonResultCode.LOGIN_ERROR);

        SessionInfo sessionInfo = redisClient.get(RedisKey.tokenKey(token), SessionInfo.class);
        LogicUtils.assertNotNull(sessionInfo, CommonResultCode.LOGIN_ERROR);
        LogicUtils.assertNotNull(sessionInfo.getId(), CommonResultCode.LOGIN_ERROR);
        return sessionInfo;
    }

    public boolean logout() {
        String token = HttpUtil.getHeader(Constant.TOKEN_HEADER_KEY);
        LogicUtils.assertNotBlank(token, CommonResultCode.LOGIN_ERROR);
        SessionInfo sessionInfo = redisClient.get(RedisKey.tokenKey(token), SessionInfo.class);
        LogicUtils.assertNotNull(sessionInfo, CommonResultCode.LOGIN_ERROR);
        redisClient.del(RedisKey.tokenKey(token));
        redisClient.del(RedisKey.tokenByIdKey(sessionInfo.getId()));
        return true;
    }

    public boolean logout(Long id) {
        String token = redisClient.get(RedisKey.tokenByIdKey(id));
        if (LogicUtils.isNotNull(token)) {
            redisClient.del(RedisKey.tokenKey(token));
            redisClient.del(RedisKey.tokenByIdKey(id));
        }
        return true;
    }

    public Boolean register(RegisterInfo registerInfo) {
        QueryWrapper<SysUser> ew = new QueryWrapper<>();
        ew.eq("invite_code", registerInfo.getInviteCode());
        SysUser sysUser = sysUserMapper.selectOne(ew);
        LogicUtils.assertNotNull(sysUser, ErrorResultCode.E000025);

        if (LogicUtils.isNotBlank(registerInfo.getSmsCode())) {
            checkSmsCode(registerInfo.getUsername(), registerInfo.getSmsCode());
        }

        checkPhoneAreaCode(registerInfo.getUsername());



        QueryWrapper<Member> memberEw = new QueryWrapper<>();
        memberEw.eq("phone", registerInfo.getUsername());
        LogicUtils.assertTrue(memberService.count(memberEw) == 0L, ErrorResultCode.E000026);

        Member member = new Member();
        member.setUserId(sysUser.getUserId());
        member.setPhone(registerInfo.getUsername());
        member.setPassword(registerInfo.getPassword());
        member.setAccountType("01".equals(sysUser.getUserType()) ? "1" : "0");
        return memberService.save(member);
    }

    private void checkPhoneAreaCode(String username) {
        QueryWrapper<SiteSmsConfig> ew = new QueryWrapper<>();
        ew.eq("status", "1");
        List<SiteSmsConfig> siteSmsConfigs = siteSmsConfigMapper.selectList(ew);
        if (siteSmsConfigs.isEmpty()) {
            return;
        }
        for (SiteSmsConfig siteSmsConfig : siteSmsConfigs) {
            if (username.startsWith(siteSmsConfig.getAreaCode())) {
                return;
            }
        }
        throw new InternalException(ErrorResultCode.E000056);

    }

    private void checkSmsCode(String registerInfo, String registerInfo1) {
        String code = redisClient.get(RedisKey.smsCodeKey(registerInfo));
        LogicUtils.assertEquals(code, registerInfo1, ErrorResultCode.E000037);
    }

    public Boolean forgetPassword(ForgetPasswordVo forgetPassword) {
        QueryWrapper<Member> ew = new QueryWrapper<>();
        ew.eq("phone", forgetPassword.getUsername());
        ew.last("limit 1");
        Member member = memberService.getOne(ew);
        LogicUtils.assertNotNull(member,ErrorResultCode.E000006);

        checkSmsCode(forgetPassword.getUsername(), forgetPassword.getSmsCode());

        UpdateWrapper<Member> uw = new UpdateWrapper<>();
        uw.set("password", forgetPassword.getPassword());
        uw.eq("id", member.getId());
        return memberService.update(uw);
    }
}
