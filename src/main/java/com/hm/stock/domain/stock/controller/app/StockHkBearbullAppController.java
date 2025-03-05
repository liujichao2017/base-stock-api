package com.hm.stock.domain.stock.controller.app;

import com.hm.stock.domain.stock.entity.StockHkBearbull;
import com.hm.stock.domain.stock.service.StockHkBearbullService;
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
@RequestMapping("/app/stock/hk/bearbull")
@Tag(name = "港股-熊牛市股接口")
public class StockHkBearbullAppController {

    @Autowired
    private StockHkBearbullService service;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @ApiResponse
    public Result<PageDate<StockHkBearbull>> selectByPage(@Parameter StockHkBearbull query,@Parameter PageParam page) {
        return Result.ok(service.selectByPage(query, page));
    }

    @GetMapping("/list")
    @Operation(summary = "列表查询")
    public Result<List<StockHkBearbull>> selectList(StockHkBearbull query) {
        return Result.ok(service.selectList(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "通过ID查询详情")
    public Result<StockHkBearbull> detail(@PathVariable("id") Long id) {
        return Result.ok(service.detail(id));
    }

}
