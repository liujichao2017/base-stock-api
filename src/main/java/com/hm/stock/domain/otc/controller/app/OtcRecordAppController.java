package com.hm.stock.domain.otc.controller.app;

import com.hm.stock.domain.otc.entity.OtcRecord;
import com.hm.stock.domain.otc.service.OtcRecordService;
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
@RequestMapping("/app/otc/record")
@Tag(name = "otc(大宗)记录表接口")
public class OtcRecordAppController {

    @Autowired
    private OtcRecordService service;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @ApiResponse
    public Result<PageDate<OtcRecord>> selectByPage(@Parameter OtcRecord query,@Parameter PageParam page) {
        return Result.ok(service.selectByPage(query, page));
    }

    @GetMapping("/list")
    @Operation(summary = "列表查询")
    public Result<List<OtcRecord>> selectList(OtcRecord query) {
        return Result.ok(service.selectList(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "通过ID查询详情")
    public Result<OtcRecord> detail(@PathVariable("id") Long id) {
        return Result.ok(service.detail(id));
    }

}
