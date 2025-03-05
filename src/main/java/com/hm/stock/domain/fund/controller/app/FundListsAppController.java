package com.hm.stock.domain.fund.controller.app;

import com.hm.stock.domain.fund.entity.FundLists;
import com.hm.stock.domain.fund.service.FundListsService;
import com.hm.stock.domain.fund.vo.FundListsVo;
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
@RequestMapping("/app/fund/lists")
@Tag(name = "基金产品接口")
public class FundListsAppController {

    @Autowired
    private FundListsService service;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @ApiResponse
    public Result<PageDate<FundLists>> selectByPage(@Parameter FundLists query,@Parameter PageParam page) {
        return Result.ok(service.selectByPage(query, page));
    }

    @GetMapping("/list")
    @Operation(summary = "列表查询")
    public Result<List<FundLists>> selectList(FundLists query) {
        return Result.ok(service.selectList(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "通过ID查询详情")
    public Result<FundLists> detail(@PathVariable("id") Long id) {
        return Result.ok(service.detail(id));
    }

    @PostMapping("/buy")
    @Operation(summary = "购买")
    public Result<Boolean> buy(@RequestBody FundListsVo fundListsVo) {
        return Result.ok(service.buy(fundListsVo));
    }

}
