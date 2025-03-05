package com.hm.stock.domain.withindays.controller.app;

import com.hm.stock.domain.loan.vo.LoanApplyVo;
import com.hm.stock.domain.withindays.entity.WithinDaysSite;
import com.hm.stock.domain.withindays.service.WithinDaysSiteService;
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
@RequestMapping("/app/within/days/site")
@Tag(name = "日内交易配置接口")
public class WithinDaysSiteAppController {

    @Autowired
    private WithinDaysSiteService service;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @ApiResponse
    public Result<PageDate<WithinDaysSite>> selectByPage(@Parameter WithinDaysSite query,@Parameter PageParam page) {
        return Result.ok(service.selectByPage(query, page));
    }

    @GetMapping("/list")
    @Operation(summary = "列表查询")
    public Result<List<WithinDaysSite>> selectList(WithinDaysSite query) {
        return Result.ok(service.selectList(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "通过ID查询详情")
    public Result<WithinDaysSite> detail(@PathVariable("id") Long id) {
        return Result.ok(service.detail(id));
    }

    @PostMapping("/apply")
    @Operation(summary = "申请日内交易")
    public Result<Boolean> apply(@RequestBody LoanApplyVo body) {
        return Result.ok(service.apply(body));
    }
}
