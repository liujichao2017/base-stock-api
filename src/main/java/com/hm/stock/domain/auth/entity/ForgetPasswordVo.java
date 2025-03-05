package com.hm.stock.domain.auth.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ForgetPasswordVo {
    /**
     * 用户名
     */
    @Schema(title = "账户")
    @NotBlank
    private String username;

    /**
     * 短信验证码
     */
    @Schema(title = "短信验证码")
    @NotBlank
    private String smsCode;

    @Schema(title = "新密码")
    @NotBlank
    private String password;
}
