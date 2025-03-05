package com.hm.stock.domain.coin.controller.app;

import com.hm.stock.domain.coin.entity.CoinKline;
import com.hm.stock.domain.coin.service.CoinKlineService;
import com.hm.stock.domain.coin.vo.CoinKlineVo;
import com.hm.stock.modules.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/coin/kline")
@Tag(name = "虚拟币K线")
public class CoinKlineAppController {

    @Autowired
    private CoinKlineService service;


    @GetMapping
    @Operation(summary = "查询K线")
    public Result<String> kline(@Parameter CoinKlineVo query) {
        return Result.ok(service.kline(query,true));
    }


}
