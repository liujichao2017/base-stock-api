package com.hm.stock.domain.ipo.controller.app;

import com.hm.stock.domain.ipo.entity.IpoRecord;
import com.hm.stock.domain.ipo.service.IpoRecordService;
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
@RequestMapping("/app/ipo/record")
@Tag(name = "IPO(新股)申购记录表接口")
public class IpoRecordAppController {

    @Autowired
    private IpoRecordService service;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @ApiResponse
    public Result<PageDate<IpoRecord>> selectByPage(@Parameter IpoRecord query,@Parameter PageParam page) {
        return Result.ok(service.selectByPage(query, page));
    }

    @GetMapping("/list")
    @Operation(summary = "列表查询")
    public Result<List<IpoRecord>> selectList(IpoRecord query) {
        return Result.ok(service.selectList(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "通过ID查询详情")
    public Result<IpoRecord> detail(@PathVariable("id") Long id) {
        return Result.ok(service.detail(id));
    }


    @PostMapping("/payment/{id}")
    @Operation(summary = "新股缴纳接口",description = "使用条件: status = 2 and totalAmt > transferAmt")
    public Result<Boolean> payment(@PathVariable("id") Long id) {
        return Result.ok(service.payment(id));
    }

}
