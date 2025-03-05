package com.hm.stock.domain.fund.controller.api;

import com.hm.stock.domain.fund.entity.FundLists;
import com.hm.stock.domain.fund.service.FundListsService;
import com.hm.stock.domain.fund.service.MemberFundRecordService;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fund/lists")
@Tag(name = "基金管理")
public class FundApiController {


    @Autowired
    private FundListsService service;


    @GetMapping("/compute")
    @Operation(summary = "结算收益")
    @ApiResponse
    public Result<Boolean> compute(@Parameter(description = "开始结算时间点: 前几天,0为当天,默认0") @RequestParam(value = "days", defaultValue = "0") Integer days,
                                   @Parameter(description = "时间 时分: 00:00, 默认 00:00") @RequestParam(value = "date", defaultValue = "00:00") String date) {
        return Result.ok(service.compute(days,date));
    }


}
