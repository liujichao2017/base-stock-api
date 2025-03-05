package com.hm.stock.domain.member.controller.app;

import com.hm.stock.domain.auth.entity.SessionInfo;
import com.hm.stock.domain.member.entity.MemberRecharge;
import com.hm.stock.domain.member.service.MemberRechargeService;
import com.hm.stock.domain.member.vo.MemberRechargeVo;
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
@RequestMapping("/app/member/recharge")
@Tag(name = "用户充值表管理接口")
public class MemberRechargeAppController {

    @Autowired
    private MemberRechargeService service;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @ApiResponse
    public Result<PageDate<MemberRecharge>> selectByPage(@Parameter MemberRecharge query,@Parameter PageParam page) {
        SessionInfo instance = SessionInfo.getInstance();
        query.setMemberId(instance.getId());
        return Result.ok(service.selectByPage(query, page));
    }

    @PostMapping("/in")
    @Operation(summary = "提交充值单")
    public Result<Long> add(@RequestBody MemberRechargeVo body) {
        return Result.ok(service.add(body));
    }

    @PostMapping("/in")
    @Operation(summary = "提交量化充值单")
    public Result<Long> add(@RequestBody MemberRechargeVo body) {
        return Result.ok(service.add(body));
    }

}
