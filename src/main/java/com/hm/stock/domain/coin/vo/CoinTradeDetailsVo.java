package com.hm.stock.domain.coin.vo;

import com.hm.stock.domain.coin.entity.CoinTradeDetails;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class CoinTradeDetailsVo {
    private List<CoinTradeDetails> buy = new ArrayList<>();

    private List<CoinTradeDetails> sell = new ArrayList<>();
}
