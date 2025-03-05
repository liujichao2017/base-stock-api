package com.hm.stock.domain.coin.controller.api;

import cn.hutool.extra.spring.SpringUtil;
import com.hm.stock.domain.coin.entity.CoinSpontaneousRobot;
import com.hm.stock.domain.coin.service.CoinSpontaneousRobotService;
import com.hm.stock.domain.coin.service.CoinSymbolsService;
import com.hm.stock.domain.coin.vo.CoinKlineAdVo;
import com.hm.stock.domain.coin.vo.CoinKlineVo;
import com.hm.stock.domain.coin.vo.GenSpontaneousRobotVo;
import com.hm.stock.domain.coin.wsclient.CoinWebSocketClient;
import com.hm.stock.modules.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coin/symbols")
@Tag(name = "虚拟币-同步")
public class CoinSymbolsApiController {

    @Autowired
    private CoinSymbolsService service;


    @Autowired
    private CoinSpontaneousRobotService robotService;

    @GetMapping("/syncSymbols")
    @Operation(summary = "同步币")
    @ApiResponse
    public Result syncSymbols() {
        service.syncSymbols();
        return Result.ok();
    }

    @GetMapping("/subscribeSymbols")
    @Operation(summary = "订阅币")
    @ApiResponse
    public Result subscribeSymbols() {
        service.subscribeSymbols(SpringUtil.getBean(CoinWebSocketClient.class), false);
        return Result.ok();
    }

    @GetMapping("/kline")
    @Operation(summary = "K线")
    @ApiResponse
    public Result<CoinKlineAdVo> subscribeSymbols(CoinKlineVo coinKlineVo) {
        return Result.ok(service.kline(coinKlineVo));
    }

    @PostMapping("/spontaneous/gen")
    @Operation(summary = "自发布生成数据")
    @ApiResponse
    public Result<Boolean> genSpontaneous(@RequestBody GenSpontaneousRobotVo coinKlineVo) {
        return Result.ok(robotService.genSpontaneous(coinKlineVo));
    }
}
