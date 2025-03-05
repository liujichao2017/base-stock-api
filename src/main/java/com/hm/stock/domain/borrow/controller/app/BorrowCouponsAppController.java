package com.hm.stock.domain.borrow.controller.app;

import com.hm.stock.domain.borrow.entity.BorrowCoupons;
import com.hm.stock.domain.borrow.service.BorrowCouponsService;
import com.hm.stock.domain.borrow.vo.BorrowBuyVo;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/app/borrow/coupons")
@Tag(name = "存股借卷配置接口")
public class BorrowCouponsAppController {

    @Autowired
    private BorrowCouponsService service;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @ApiResponse
    public Result<PageDate<BorrowCoupons>> selectByPage(@Parameter BorrowCoupons query,@Parameter PageParam page) {
        return Result.ok(service.selectByPage(query, page));
    }

    @GetMapping("/list")
    @Operation(summary = "列表查询")
    public Result<List<BorrowCoupons>> selectList(BorrowCoupons query) {
        return Result.ok(service.selectList(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "通过ID查询详情")
    public Result<BorrowCoupons> detail(@PathVariable("id") Long id) {
        return Result.ok(service.detail(id));
    }

    @PostMapping("/buy")
    @Operation(summary = "购买借劵")
    public Result<Boolean> buy(@RequestBody @Valid BorrowBuyVo body) {
        return Result.ok(service.buy(body));
    }

}
