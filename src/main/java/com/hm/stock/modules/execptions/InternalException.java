package com.hm.stock.modules.execptions;

import lombok.Getter;

@Getter
public class InternalException extends RuntimeException {
    private final ResultCode resultCode;
    private final Object[] params;

    public InternalException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
        this.params = new Object[0];
    }

    public InternalException(ResultCode resultCode, Object[] params) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
        this.params = params;
    }
}
