package com.hm.stock.domain.coin.controller.app;

import com.hm.stock.domain.coin.entity.CoinDelivery;
import com.hm.stock.domain.coin.service.CoinDeliveryService;
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
@RequestMapping("/app/coin/delivery")
@Tag(name = "虚拟币交割方案接口")
public class CoinDeliveryAppController {

    @Autowired
    private CoinDeliveryService service;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @ApiResponse
    public Result<PageDate<CoinDelivery>> selectByPage(@Parameter CoinDelivery query,@Parameter PageParam page) {
        return Result.ok(service.selectByPage(query, page));
    }

    @GetMapping("/list")
    @Operation(summary = "列表查询")
    public Result<List<CoinDelivery>> selectList(CoinDelivery query) {
        return Result.ok(service.selectList(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "通过ID查询详情")
    public Result<CoinDelivery> detail(@PathVariable("id") Long id) {
        return Result.ok(service.detail(id));
    }

}
