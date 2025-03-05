package com.hm.stock.domain.member.controller.api;

import com.hm.stock.domain.member.entity.Member;
import com.hm.stock.domain.member.entity.MemberFundsLogs;
import com.hm.stock.domain.member.service.MemberService;
import com.hm.stock.domain.member.vo.FundConvertVo;
import com.hm.stock.domain.member.vo.MemberRealVo;
import com.hm.stock.domain.member.vo.MemberVo;
import com.hm.stock.domain.stock.entity.Stock;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/member")
@Tag(name = "会员管理接口")
public class MemberApiController {

    @Autowired
    private MemberService service;


    @GetMapping("/change/{id}")
    @Operation(summary = "用户状态刷新")
    public Result<Stock> memberChange(@PathVariable("id") Long memberId) {
        service.memberChange(memberId);
        return Result.ok();
    }
}
