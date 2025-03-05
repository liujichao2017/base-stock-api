package com.hm.stock.domain.coin.controller.app;

import com.hm.stock.domain.coin.entity.CoinAssets;
import com.hm.stock.domain.coin.service.CoinAssetsService;
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
@RequestMapping("/app/coin/assets")
@Tag(name = "虚拟币资金接口")
public class CoinAssetsAppController {

    @Autowired
    private CoinAssetsService service;


    @GetMapping("/my/{marketId}")
    @Operation(summary = "我的币资产")
    public Result<List<CoinAssets>> my(@PathVariable("marketId")Long marketId) {
        return Result.ok(service.my(marketId));
    }


}
