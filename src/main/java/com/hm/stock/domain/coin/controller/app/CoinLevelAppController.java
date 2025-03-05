package com.hm.stock.domain.coin.controller.app;

import com.hm.stock.domain.coin.entity.CoinLevel;
import com.hm.stock.domain.coin.service.CoinLevelService;
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
@RequestMapping("/app/coin/level")
@Tag(name = "虚拟币杠杆接口")
public class CoinLevelAppController {

    @Autowired
    private CoinLevelService service;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @ApiResponse
    public Result<PageDate<CoinLevel>> selectByPage(@Parameter CoinLevel query,@Parameter PageParam page) {
        return Result.ok(service.selectByPage(query, page));
    }

    @GetMapping("/list")
    @Operation(summary = "列表查询")
    public Result<List<CoinLevel>> selectList(CoinLevel query) {
        return Result.ok(service.selectList(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "通过ID查询详情")
    public Result<CoinLevel> detail(@PathVariable("id") Long id) {
        return Result.ok(service.detail(id));
    }

}
