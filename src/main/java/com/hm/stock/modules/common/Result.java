package com.hm.stock.modules.common;

import com.hm.stock.modules.execptions.CommonResultCode;
import com.hm.stock.modules.execptions.ResultCode;
import com.hm.stock.modules.i18n.I18nUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.Locale;

@Getter
public class Result<T> {
    @Schema(title = "响应码")
    private int code;
    @Schema(title = "提示")
    private String message;
    @Schema(title = "数据")
    private T data;

    private Result() {

    }

    public static Result ok() {
        Result results = new Result();
        CommonResultCode resultCode = CommonResultCode.OK;
        results.code = resultCode.getCode();
        results.message = I18nUtil.getMessage(resultCode.name());
        return results;
    }

    public static <T> Result<T> ok(T data) {
        Result<T> results = new Result<>();
        CommonResultCode resultCode = CommonResultCode.OK;
        results.code = resultCode.getCode();
        results.message = I18nUtil.getMessage(resultCode.name());
        results.data = data;
        return results;
    }

    public static <T> Result<T> ok(Locale locale) {
        Result<T> results = new Result<>();
        CommonResultCode resultCode = CommonResultCode.OK;
        results.code = resultCode.getCode();
        results.message = I18nUtil.getMessage(resultCode.name(), locale);
        return results;
    }

    public static Result error(ResultCode resultCode) {
        Result result = new Result();
        result.code = resultCode.getCode();
        result.message = I18nUtil.getMessage(resultCode.name());
        return result;
    }

    public static Result error(ResultCode resultCode, Locale locale) {
        Result result = new Result();
        result.code = resultCode.getCode();
        result.message = I18nUtil.getMessage(resultCode.name(), locale);
        return result;
    }

    public static Result error(ResultCode resultCode, Object[] param) {
        Result result = new Result();
        result.code = resultCode.getCode();
        result.message = I18nUtil.getMessage(resultCode.name(), param);
        return result;
    }
}
