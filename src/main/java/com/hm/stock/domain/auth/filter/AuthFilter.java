package com.hm.stock.domain.auth.filter;

import com.hm.stock.domain.auth.config.UrlWhitelist;
import com.hm.stock.domain.auth.entity.SessionInfo;
import com.hm.stock.domain.auth.service.AuthService;
import com.hm.stock.domain.member.mapper.MemberMapper;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.execptions.CommonResultCode;
import com.hm.stock.modules.execptions.InternalException;
import com.hm.stock.modules.utils.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);
    @Resource
    public UrlWhitelist whitelist;

    @Resource
    private AuthService authService;

    @Resource
    private MemberMapper memberMapper;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws
            IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        HttpUtil.setCorsHeader(response);

        String requestUri = request.getRequestURI();
        if (whitelist.contains(requestUri)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            SessionInfo sessionInfo = authService.getCurrentSession();
            LogicUtils.assertNotNull(sessionInfo, CommonResultCode.LOGIN_ERROR);
//            Member member = memberMapper.selectById(sessionInfo.getId());
//            LogicUtils.assertNotNull(member,CommonResultCode.LOGIN_ERROR);
//            LogicUtils.assertTrue(YNEnum.yes(member.getIsLogin()), CommonResultCode.LOGIN_ERROR);
        } catch (InternalException e) {
            log.error("内部异常, 请求URL: {} ,{}", requestUri, e.getResultCode().getMessage());
            log.debug("内部异常, ", e);
            HttpUtil.writerErrMsg(e.getResultCode());
            return;
        } catch (Exception e) {
            log.error("登录错误: \n", e);
            HttpUtil.writerErrMsg(CommonResultCode.INTERNAL_ERROR);
            return;
        }

        chain.doFilter(request, response);
    }


}
