package com.hm.stock.domain.ipo.controller.app;

import com.hm.stock.domain.ipo.entity.Ipo;
import com.hm.stock.domain.ipo.service.IpoService;
import com.hm.stock.domain.ipo.vo.IpoBuyVo;
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
@RequestMapping("/app/ipo")
@Tag(name = "IPO(新股)表接口")
public class IpoAppController {

    @Autowired
    private IpoService service;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @ApiResponse
    public Result<PageDate<Ipo>> selectByPage(@Parameter Ipo query,@Parameter PageParam page) {
        return Result.ok(service.selectByPage(query, page));
    }

    @GetMapping("/list")
    @Operation(summary = "列表查询")
    public Result<List<Ipo>> selectList(Ipo query) {
        return Result.ok(service.selectList(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "通过ID查询详情")
    public Result<Ipo> detail(@PathVariable("id") Long id) {
        return Result.ok(service.detail(id));
    }

    @PostMapping("/buy")
    @Operation(summary = "新股申购")
    public Result<Boolean> buy(@RequestBody IpoBuyVo body) {
        return Result.ok(service.buy(body));
    }


}
