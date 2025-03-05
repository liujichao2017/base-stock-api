package com.hm.stock.domain.withindays.controller.app;

import com.hm.stock.domain.loan.vo.LoanApplyVo;
import com.hm.stock.domain.withindays.entity.WithinDaysRecord;
import com.hm.stock.domain.withindays.service.WithinDaysRecordService;
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
@RequestMapping("/app/within/days/record")
@Tag(name = "用户日内交易记录接口")
public class WithinDaysRecordAppController {

    @Autowired
    private WithinDaysRecordService service;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @ApiResponse
    public Result<PageDate<WithinDaysRecord>> selectByPage(@Parameter WithinDaysRecord query,@Parameter PageParam page) {
        return Result.ok(service.selectByPage(query, page));
    }

    @GetMapping("/list")
    @Operation(summary = "列表查询")
    public Result<List<WithinDaysRecord>> selectList(WithinDaysRecord query) {
        return Result.ok(service.selectList(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "通过ID查询详情")
    public Result<WithinDaysRecord> detail(@PathVariable("id") Long id) {
        return Result.ok(service.detail(id));
    }

}
