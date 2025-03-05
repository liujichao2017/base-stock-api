package com.hm.stock.domain.coin.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class CoinKlineAdVo {
    private List<String> time = new ArrayList<>();

    private List<List<BigDecimal>> infos = new ArrayList<>();
}
