package com.hm.stock.domain.coin.controller.app;

import com.hm.stock.domain.coin.entity.CoinContractDelegation;
import com.hm.stock.domain.coin.service.CoinContractDelegationService;
import com.hm.stock.domain.coin.vo.BuyCoinContractVo;
import com.hm.stock.domain.coin.vo.CalculateContractParamVo;
import com.hm.stock.domain.coin.vo.CalculateContractVo;
import com.hm.stock.domain.coin.vo.UpdateContractVo;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/coin/contract/delegation")
@Tag(name = "虚拟币合约委托接口")
public class CoinContractDelegationAppController {

    @Autowired
    private CoinContractDelegationService service;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @ApiResponse
    public Result<PageDate<CoinContractDelegation>> selectByPage(@Parameter CoinContractDelegation query,
                                                                 @Parameter PageParam page) {
        return Result.ok(service.selectByPage(query, page));
    }

    @PostMapping("/buy")
    @Operation(summary = "购买合约")
    public Result<Boolean> buy(@RequestBody BuyCoinContractVo body) {
        return Result.ok(service.buy(body));
    }

    @PostMapping("/sell/{id}")
    @Operation(summary = "卖出合约")
    public Result<Boolean> sell(@PathVariable Long id) {
        return Result.ok(service.sell(id));
    }

    @PostMapping("/updateProfitLossLimit")
    @Operation(summary = "合约设置/更新止盈止亏价")
    public Result<Boolean> updateProfitLossLimit(@PathVariable UpdateContractVo body) {
        return Result.ok(service.updateProfitLossLimit(body));
    }

    @PostMapping("/calculate")
    @Operation(summary = "计算费用")
    public Result<CalculateContractVo> calculate(@RequestBody CalculateContractParamVo body) {
        return Result.ok(service.calculate(body));
    }

    @GetMapping("/cancel/{id}")
    @Operation(summary = "取消合约")
    public Result<Boolean> cancel(@PathVariable("id") Long id) {
        return Result.ok(service.cancel(id));
    }


}
