package com.hm.stock.domain.auth.controller;

import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.hm.stock.domain.auth.entity.ForgetPasswordVo;
import com.hm.stock.domain.auth.entity.LoginInfo;
import com.hm.stock.domain.auth.entity.LoginResult;
import com.hm.stock.domain.auth.entity.RegisterInfo;
import com.hm.stock.domain.auth.service.AuthService;
import com.hm.stock.modules.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
@Tag(name = "移动端登录管理接口")
@ApiSort(1)
public class LoginController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "移动端登录")
    public Result<LoginResult> login(@RequestBody LoginInfo loginInfo) {
        LoginResult result = authService.login(loginInfo);
        return Result.ok(result);
    }


    @PostMapping("/logout")
    @Operation(summary = "移动端登出")
    public Result<Boolean> login() {
        return Result.ok(authService.logout());
    }


    @PostMapping("/register")
    @Operation(summary = "移动端注册")
    public Result<Boolean> register(@RequestBody RegisterInfo registerInfo) {
        return Result.ok(authService.register(registerInfo));
    }

    @PostMapping("/forgetPassword")
    @Operation(summary = "忘记密码")
    public Result<Boolean> forgetPassword(@RequestBody ForgetPasswordVo forgetPassword) {
        return Result.ok(authService.forgetPassword(forgetPassword));
    }


}
