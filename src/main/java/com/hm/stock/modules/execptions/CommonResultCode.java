package com.hm.stock.modules.execptions;

import lombok.Getter;

@Getter
public enum CommonResultCode implements ResultCode{
    OK(200, "成功"),
    PARAM_ERROR(400, "参数校验异常"),
    LOGIN_ERROR(401, "未登录"),
    AUTH_ERROR(403, "权限不足"),
    AUT_ERROR(405,"非法请求"),
    INTERNAL_ERROR(500, "系统正忙,请稍后重试"),
    ;


    private final int code;

    private final String message;

    CommonResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
