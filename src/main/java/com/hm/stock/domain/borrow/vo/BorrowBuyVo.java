package com.hm.stock.domain.borrow.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class BorrowBuyVo {
    @NotNull
    private Long id;

    @NotNull
    private Long num;
}
