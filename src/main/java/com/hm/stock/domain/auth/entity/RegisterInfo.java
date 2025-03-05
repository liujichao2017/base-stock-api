package com.hm.stock.domain.auth.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(title = "登录")
public class RegisterInfo {
    /**
     * 用户名
     */
    @Schema(title = "账户")
    @NotBlank
    private String username;
    /**
     * 密码
     */
    @Schema(title = "密码")
    @NotBlank
    private String password;

    /**
     * 邀请码
     */
    @Schema(title = "密码")
    @NotBlank
    private String inviteCode;

    /**
     * 短信验证码
     */
    @Schema(title = "短信验证码")
    private String smsCode;
}
