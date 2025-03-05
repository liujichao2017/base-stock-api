package com.hm.stock.domain.loan.controller.app;

import com.hm.stock.domain.loan.entity.LoanConfiguration;
import com.hm.stock.domain.loan.service.LoanConfigurationService;
import com.hm.stock.domain.loan.vo.LoanApplyVo;
import com.hm.stock.domain.loan.vo.LoanConfigVo;
import com.hm.stock.modules.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/loan/configuration")
@Tag(name = "贷款配置接口")
public class LoanConfigurationAppController {

    @Autowired
    private LoanConfigurationService service;

    @GetMapping("/list")
    @Operation(summary = "列表查询")
    public Result<List<LoanConfiguration>> selectList(LoanConfiguration query) {
        return Result.ok(service.selectList(query));
    }

    @GetMapping("/{marketId}")
    @Operation(summary = "通过主市场ID查询贷款配置")
    public Result<LoanConfigVo> detail(@PathVariable("marketId") @Parameter(description = "主市场ID") Long marketId) {
        return Result.ok(service.detail(marketId));
    }


    @PostMapping("/apply")
    @Operation(summary = "申请贷款")
    public Result<Boolean> apply(@RequestBody LoanApplyVo body) {
        return Result.ok(service.apply(body));
    }


}
