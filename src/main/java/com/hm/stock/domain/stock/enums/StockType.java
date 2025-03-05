package com.hm.stock.domain.stock.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StockType {
    STOCK("1"),
    INDEX("2");

    private final String code;
}
