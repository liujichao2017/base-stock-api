package com.hm.stock.domain.stock.controller.app;

import com.hm.stock.domain.stock.entity.StockMlMalaysiawarrants;
import com.hm.stock.domain.stock.service.StockMlMalaysiawarrantsService;
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
@RequestMapping("/app/stock/ml/malaysiawarrants")
@Tag(name = "malaysiawarrants接口")
public class StockMlMalaysiawarrantsAppController {

    @Autowired
    private StockMlMalaysiawarrantsService service;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @ApiResponse
    public Result<PageDate<StockMlMalaysiawarrants>> selectByPage(@Parameter StockMlMalaysiawarrants query,@Parameter PageParam page) {
        return Result.ok(service.selectByPage(query, page));
    }

    @GetMapping("/list")
    @Operation(summary = "列表查询")
    public Result<List<StockMlMalaysiawarrants>> selectList(StockMlMalaysiawarrants query) {
        return Result.ok(service.selectList(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "通过ID查询详情")
    public Result<StockMlMalaysiawarrants> detail(@PathVariable("id") Long id) {
        return Result.ok(service.detail(id));
    }

}
