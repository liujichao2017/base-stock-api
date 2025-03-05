package com.hm.stock.domain.auth.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "登录")
public class LoginResult {
    /**
     * token
     */
    @Schema(title = "token")
    private String token;
    /**
     * 用户id
     */
    @Schema(title = "用户ID")
    private Long id;
}
