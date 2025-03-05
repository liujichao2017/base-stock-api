package com.hm.stock.domain.ws.entity;

import com.hm.stock.modules.execptions.CommonResultCode;
import com.hm.stock.modules.execptions.ResultCode;
import lombok.Data;

@Data
public class WsResult {
    private String func;

    private int code;

    private Object data;

    public static WsResult get(String func) {
        WsResult wsResult = new WsResult();
        wsResult.setFunc(func);
        wsResult.setCode(CommonResultCode.OK.getCode());
        return wsResult;
    }

    public static WsResult get(String func, ResultCode code) {
        WsResult wsResult = new WsResult();
        wsResult.setFunc(func);
        wsResult.setCode(code.getCode());
        return wsResult;
    }

    public static WsResult get(String func, Object data) {
        WsResult wsResult = get(func);
        wsResult.setData(data);
        return wsResult;
    }

    public static WsResult get(String func, ResultCode code, Object data) {
        WsResult wsResult = new WsResult();
        wsResult.setFunc(func);
        wsResult.setCode(code.getCode());
        wsResult.setData(data);
        return wsResult;
    }
}
