package com.hm.stock.domain.stock.controller.app;

import com.hm.stock.domain.stock.entity.Stock;
import com.hm.stock.domain.stock.entity.StockOption;
import com.hm.stock.domain.stock.service.StockOptionService;
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
@RequestMapping("/app/stock/option")
@Tag(name = "股票自选管理接口")
public class StockOptionAppController {

    @Autowired
    private StockOptionService service;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @ApiResponse
    public Result<PageDate<Stock>> selectByPage(@Parameter StockOption query,@Parameter PageParam page) {
        return Result.ok(service.selectByPage(query, page));
    }

    @GetMapping("/list")
    @Operation(summary = "列表查询")
    public Result<List<Stock>> selectList(StockOption query) {
        return Result.ok(service.selectList(query));
    }

    @GetMapping("/is/{gid}")
    @Operation(summary = "是否为自选")
    public Result<Boolean> isOption(@PathVariable("gid") @Parameter(description = "股票Gid") String gid){
        return Result.ok(service.isOption(gid));
    }

    @PostMapping("/{gid}")
    @Operation(summary = "添加自选股票")
    public Result<Boolean> save(@PathVariable("gid") @Parameter(description = "股票Gid") String gid) {
        return Result.ok(service.save(gid));
    }

    @DeleteMapping("/{gid}")
    @Operation(summary = "删除自选股票")
    public Result<Boolean> delete(@PathVariable("gid") @Parameter(description = "股票Gid") String gid) {
        return Result.ok(service.delete(gid));
    }
}
