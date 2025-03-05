package com.hm.stock.domain.borrow.controller.app;

import com.hm.stock.domain.borrow.entity.MemberBorrowRecord;
import com.hm.stock.domain.borrow.service.MemberBorrowRecordService;
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
@RequestMapping("/app/member/borrow/record")
@Tag(name = "接口")
public class MemberBorrowRecordAppController {

    @Autowired
    private MemberBorrowRecordService service;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @ApiResponse
    public Result<PageDate<MemberBorrowRecord>> selectByPage(@Parameter MemberBorrowRecord query,@Parameter PageParam page) {
        return Result.ok(service.selectByPage(query, page));
    }

    @GetMapping("/list")
    @Operation(summary = "列表查询")
    public Result<List<MemberBorrowRecord>> selectList(MemberBorrowRecord query) {
        return Result.ok(service.selectList(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "通过ID查询详情")
    public Result<MemberBorrowRecord> detail(@PathVariable("id") Long id) {
        return Result.ok(service.detail(id));
    }

}
