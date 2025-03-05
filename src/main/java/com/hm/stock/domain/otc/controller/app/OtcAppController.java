package com.hm.stock.domain.otc.controller.app;

import com.hm.stock.domain.otc.entity.Otc;
import com.hm.stock.domain.otc.entity.OtcRecord;
import com.hm.stock.domain.otc.service.OtcService;
import com.hm.stock.domain.otc.vo.OtcBuyVo;
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
@RequestMapping("/app/otc")
@Tag(name = "otc(大宗)表接口")
public class OtcAppController {

    @Autowired
    private OtcService service;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @ApiResponse
    public Result<PageDate<Otc>> selectByPage(@Parameter Otc query,@Parameter PageParam page) {
        return Result.ok(service.selectByPage(query, page));
    }

    @GetMapping("/list")
    @Operation(summary = "列表查询")
    public Result<List<Otc>> selectList(Otc query) {
        return Result.ok(service.selectList(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "通过ID查询详情")
    public Result<Otc> detail(@PathVariable("id") Long id) {
        return Result.ok(service.detail(id));
    }

    @PostMapping("/buy")
    @Operation(summary = "大宗申购")
    public Result<Boolean> buy(@RequestBody OtcBuyVo buyVo) {
        return Result.ok(service.buy(buyVo));
    }

}
