package com.hm.stock.domain.dailylimit.controller.app;

import com.hm.stock.domain.dailylimit.entity.StockDailyLimit;
import com.hm.stock.domain.dailylimit.service.StockDailyLimitService;
import com.hm.stock.domain.dailylimit.vo.DailyLimitVo;
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
@RequestMapping("/app/stock/daily/limit")
@Tag(name = "涨停表接口")
public class StockDailyLimitAppController {

    @Autowired
    private StockDailyLimitService service;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @ApiResponse
    public Result<PageDate<StockDailyLimit>> selectByPage(@Parameter StockDailyLimit query, @Parameter PageParam page) {
        return Result.ok(service.selectByPage(query, page));
    }

    @GetMapping("/list")
    @Operation(summary = "列表查询")
    public Result<List<StockDailyLimit>> selectList(StockDailyLimit query) {
        return Result.ok(service.selectList(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "通过ID查询详情")
    public Result<StockDailyLimit> detail(@PathVariable("id") Long id) {
        return Result.ok(service.detail(id));
    }


    @PostMapping("/buy")
    @Operation(summary = "抢筹购买")
    public Result<Boolean> buy(@RequestBody DailyLimitVo dailyLimitVo) {
        return Result.ok(service.buy(dailyLimitVo));
    }
}
