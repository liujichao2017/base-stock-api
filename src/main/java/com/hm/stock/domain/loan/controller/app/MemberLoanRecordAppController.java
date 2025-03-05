package com.hm.stock.domain.loan.controller.app;

import com.hm.stock.domain.loan.entity.MemberLoanRecord;
import com.hm.stock.domain.loan.service.MemberLoanRecordService;
import com.hm.stock.domain.loan.vo.CountLoanVo;
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
@RequestMapping("/app/member/loan/record")
@Tag(name = "用户贷款申请表接口")
public class MemberLoanRecordAppController {

    @Autowired
    private MemberLoanRecordService service;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @ApiResponse
    public Result<PageDate<MemberLoanRecord>> selectByPage(@Parameter MemberLoanRecord query,@Parameter PageParam page) {
        return Result.ok(service.selectByPage(query, page));
    }

    @GetMapping("/count")
    @Operation(summary = "贷款统计")
    @ApiResponse
    public Result<CountLoanVo> countLoan() {
        return Result.ok(service.countLoan());
    }

    @GetMapping("/list")
    @Operation(summary = "列表查询")
    public Result<List<MemberLoanRecord>> selectList(MemberLoanRecord query) {
        return Result.ok(service.selectList(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "通过ID查询详情")
    public Result<MemberLoanRecord> detail(@PathVariable("id") Long id) {
        return Result.ok(service.detail(id));
    }

    @GetMapping("/repayment/{id}")
    @Operation(summary = "申请还款")
    public Result<Boolean> repayment(@PathVariable("id") Long id) {
        return Result.ok(service.repayment(id));
    }

}
