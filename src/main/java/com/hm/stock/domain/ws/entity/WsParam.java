package com.hm.stock.domain.ws.entity;

import lombok.Data;

@Data
public class WsParam {
    private String func;

    private Object param;
}
