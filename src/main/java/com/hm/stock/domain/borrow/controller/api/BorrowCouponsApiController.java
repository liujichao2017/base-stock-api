package com.hm.stock.domain.borrow.controller.api;

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
@RequestMapping("/api/borrow/coupons")
@Tag(name = "存股借卷")
public class BorrowCouponsApiController {

    @Autowired
    private BorrowCouponsService service;

    @GetMapping("/compute")
    @Operation(summary = "结算收益")
    @ApiResponse
    public Result<Boolean> compute() {
        return Result.ok(service.compute());
    }

}
