package com.hm.stock.domain.member.controller.app;

import com.hm.stock.domain.member.entity.MemberBank;
import com.hm.stock.domain.member.service.MemberBankService;
import com.hm.stock.modules.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/member/bank")
@Tag(name = "用户银行卡管理接口")
public class MemberBankAppController {

    @Autowired
    private MemberBankService service;

    @GetMapping("/{id}")
    @Operation(summary = "查询用户银行卡")
    public Result<MemberBank> detail() {
        return Result.ok(service.detail());
    }


    @PostMapping("/{id}")
    @Operation(summary = "添加/修改银行卡")
    public Result<MemberBank> save(@RequestBody MemberBank memberBank) {
        return Result.ok(service.saveByMemberId(memberBank));
    }
}
