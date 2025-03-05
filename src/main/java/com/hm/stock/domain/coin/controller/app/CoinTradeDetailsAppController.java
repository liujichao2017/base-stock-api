package com.hm.stock.domain.coin.controller.app;

import com.hm.stock.domain.coin.entity.CoinTradeDetails;
import com.hm.stock.domain.coin.service.CoinTradeDetailsService;
import com.hm.stock.domain.coin.vo.CoinTradeDetailsVo;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/app/coin/trade/details")
@Tag(name = "虚拟币交易记录")
public class CoinTradeDetailsAppController {

    @Autowired
    private CoinTradeDetailsService service;

    @GetMapping("/{symbol}")
    @Operation(summary = "列表查询")
    public Result<CoinTradeDetailsVo> selectList(@PathVariable("symbol")String symbol) {
        return Result.ok(service.selectList(symbol));
    }
}
