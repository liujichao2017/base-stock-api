package com.hm.stock.domain.aitrade.controller.app;

import com.hm.stock.domain.aitrade.entity.AiTradeRecord;
import com.hm.stock.domain.aitrade.service.AiTradeRecordService;
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
@RequestMapping("/app/ai/trade/record")
@Tag(name = "AI交易记录表接口")
public class AiTradeRecordAppController {

    @Autowired
    private AiTradeRecordService service;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @ApiResponse
    public Result<PageDate<AiTradeRecord>> selectByPage(@Parameter AiTradeRecord query,@Parameter PageParam page) {
        return Result.ok(service.selectByPage(query, page));
    }

    @GetMapping("/list")
    @Operation(summary = "列表查询")
    public Result<List<AiTradeRecord>> selectList(AiTradeRecord query) {
        return Result.ok(service.selectList(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "通过ID查询详情")
    public Result<AiTradeRecord> detail(@PathVariable("id") Long id) {
        return Result.ok(service.detail(id));
    }

}
