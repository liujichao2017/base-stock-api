package com.hm.stock.domain.stock.controller.app;

import com.hm.stock.domain.auth.entity.SessionInfo;
import com.hm.stock.domain.stock.entity.MemberPosition;
import com.hm.stock.domain.stock.service.MemberPositionService;
import com.hm.stock.domain.stock.vo.MemberPositionVo;
import com.hm.stock.domain.stock.vo.PositionQueryVo;
import com.hm.stock.domain.stock.vo.StockBuyVo;
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
@RequestMapping("/app/member/position")
@Tag(name = "用户持仓表管理接口")
public class MemberPositionAppController {

    @Autowired
    private MemberPositionService service;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @ApiResponse
    public Result<PageDate<MemberPositionVo>> selectByPage(@Parameter PositionQueryVo query, @Parameter PageParam page) {
        return Result.ok(service.selectByPage(query, page));
    }

    @PostMapping("/buy")
    @Operation(summary = "购买股票")
    @ApiResponse
    public Result<Boolean> buy(@RequestBody @Parameter StockBuyVo stockBuyVo) {
        return Result.ok(service.buy(stockBuyVo));
    }

    @PutMapping("/sell/{id}")
    @Operation(summary = "卖出股票")
    @ApiResponse
    public Result<Boolean> sell(@PathVariable("id") Long id) {
        return Result.ok(service.sell(id));
    }

}
