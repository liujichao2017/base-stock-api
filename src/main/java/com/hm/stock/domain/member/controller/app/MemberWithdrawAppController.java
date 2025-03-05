package com.hm.stock.domain.member.controller.app;

import com.hm.stock.domain.member.entity.MemberWithdraw;
import com.hm.stock.domain.member.service.MemberWithdrawService;
import com.hm.stock.domain.member.vo.MemberCancelWithdrawVo;
import com.hm.stock.domain.member.vo.MemberWithdrawVo;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/member/withdraw")
@Tag(name = "用户提现表管理接口")
public class MemberWithdrawAppController {

    @Autowired
    private MemberWithdrawService service;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @ApiResponse
    public Result<PageDate<MemberWithdraw>> selectByPage(@Parameter MemberWithdraw query,@Parameter PageParam page) {
        return Result.ok(service.selectByPage(query, page));
    }

    @PostMapping("/out")
    @Operation(summary = "提交提现单")
    public Result<Long> add(@RequestBody MemberWithdrawVo body) {
        return Result.ok(service.add(body));
    }

    @PostMapping({"cancel"})
    @Operation(summary = "取消提现")
    public Result<Long> userCancel(@RequestBody MemberCancelWithdrawVo body) {
        return Result.ok(service.userCancel(body));
    }

}
