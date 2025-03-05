package com.hm.stock.domain.member.controller.app;

import com.hm.stock.domain.member.entity.Member;
import com.hm.stock.domain.member.entity.MemberFundsLogs;
import com.hm.stock.domain.member.service.MemberService;
import com.hm.stock.domain.member.vo.FundConvertVo;
import com.hm.stock.domain.member.vo.MemberRealVo;
import com.hm.stock.domain.member.vo.MemberVo;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/member")
@Tag(name = "会员管理接口")
public class MemberAppController {

    @Autowired
    private MemberService service;

    @GetMapping
    @Operation(summary = "查询登录用户信息")
    public Result<MemberVo> detail() {
        return Result.ok(service.detail());
    }

    @GetMapping("/funds")
    @Operation(summary = "资金明细")
    public Result<PageDate<MemberFundsLogs>> fundsList(@Parameter MemberFundsLogs query, @Parameter PageParam page) {
        return Result.ok(service.fundsList(query, page));
    }

    @PutMapping("/update")
    @Operation(summary = "修改用户信息,头像,密码,提现密码")
    public Result<Boolean> update(@Valid @RequestBody Member member) {
        return Result.ok(service.update(member));
    }

    @PutMapping("/convertAmt")
    @Operation(summary = "资金转换")
    public Result<Boolean> convertAmt(@Valid @RequestBody FundConvertVo fundConvertVo) {
        return Result.ok(service.convertAmt(fundConvertVo));
    }

    @PostMapping("/realName")
    @Operation(summary = "实名认证")
    public Result<Boolean> realName(@Valid @RequestBody MemberRealVo memberRealVo) {
        return Result.ok(service.realName(memberRealVo));
    }
}
