package com.hm.stock.domain.auth.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "登录")
public class LoginInfo {
    /**
     * 用户名
     */
    @Schema(title = "账户")
    private String username;
    /**
     * 密码
     */
    @Schema(title = "密码")
    private String password;
}
