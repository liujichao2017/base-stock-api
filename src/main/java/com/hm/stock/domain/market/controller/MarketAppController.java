package com.hm.stock.domain.market.controller;

import com.hm.stock.domain.market.entity.Market;
import com.hm.stock.domain.market.service.MarketService;
import com.hm.stock.modules.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/app/market")
@Tag(name = "市场管理接口")
public class MarketAppController {

    @Autowired
    private MarketService service;


    @GetMapping("/list")
    @Operation(summary = "列表查询")
    public Result<List<Market>> selectList(Market query) {
        return Result.ok(service.selectList(query));
    }


}
