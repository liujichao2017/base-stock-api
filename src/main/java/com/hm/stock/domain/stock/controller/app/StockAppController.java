package com.hm.stock.domain.stock.controller.app;

import com.hm.stock.domain.stock.entity.Stock;
import com.hm.stock.domain.stock.service.StockService;
import com.hm.stock.domain.stock.vo.StockVo;
import com.hm.stock.domain.stockdata.reset.vo.Kline;
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
@RequestMapping("/app/stock")
@Tag(name = "股票管理接口")
public class StockAppController {

    @Autowired
    private StockService service;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @ApiResponse
    public Result<PageDate<Stock>> selectByPage(@Parameter StockVo query, @Parameter PageParam page) {
        return Result.ok(service.selectByPage(query, page));
    }

    @GetMapping("/{gid}")
    @Operation(summary = "通过ID查询详情")
    public Result<Stock> detail(@PathVariable("gid") String gid) {
        return Result.ok(service.detail(gid));
    }

    @GetMapping("/kline")
    @Operation(summary = "股票K线")
    public Result<String> kline(@RequestParam("gid") String gid,@RequestParam("type") String type) {
        return Result.ok(service.kline(gid,type));
    }
}
