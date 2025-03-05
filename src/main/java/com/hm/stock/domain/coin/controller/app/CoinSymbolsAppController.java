package com.hm.stock.domain.coin.controller.app;

import com.hm.stock.domain.coin.entity.CoinSymbols;
import com.hm.stock.domain.coin.service.CoinSymbolsService;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/coin/symbols")
@Tag(name = "虚拟币-交易对")
public class CoinSymbolsAppController {

    @Autowired
    private CoinSymbolsService service;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @ApiResponse
    public Result<PageDate<CoinSymbols>> selectByPage(@Parameter CoinSymbols query,@Parameter PageParam page) {
        return Result.ok(service.selectByPage(query, page));
    }
}
