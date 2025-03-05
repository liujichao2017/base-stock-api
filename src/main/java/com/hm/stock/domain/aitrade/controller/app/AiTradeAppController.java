package com.hm.stock.domain.aitrade.controller.app;

import com.hm.stock.domain.aitrade.entity.AiTrade;
import com.hm.stock.domain.aitrade.service.AiTradeService;
import com.hm.stock.domain.aitrade.vo.AiTradeBuyVo;
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
@RequestMapping("/app/ai/trade")
@Tag(name = "AI交易表接口")
public class AiTradeAppController {

    @Autowired
    private AiTradeService service;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @ApiResponse
    public Result<PageDate<AiTrade>> selectByPage(@Parameter AiTrade query,@Parameter PageParam page) {
        return Result.ok(service.selectByPage(query, page));
    }

    @GetMapping("/list")
    @Operation(summary = "列表查询")
    public Result<List<AiTrade>> selectList(AiTrade query) {
        return Result.ok(service.selectList(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "通过ID查询详情")
    public Result<AiTrade> detail(@PathVariable("id") Long id) {
        return Result.ok(service.detail(id));
    }

    @PostMapping("/buy")
    @Operation(summary = "购买AI交易")
    public Result<Boolean> buy(@RequestBody AiTradeBuyVo body) {
        return Result.ok(service.buy(body));
    }

}
