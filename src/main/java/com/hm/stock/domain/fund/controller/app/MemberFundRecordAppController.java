package com.hm.stock.domain.fund.controller.app;

import com.hm.stock.domain.fund.entity.MemberFundRecord;
import com.hm.stock.domain.fund.service.MemberFundRecordService;
import com.hm.stock.domain.fund.vo.CountMemberFundVo;
import com.hm.stock.domain.fund.vo.MemberFundRecordVo;
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
@RequestMapping("/app/member/fund/record")
@Tag(name = "基金购买记录接口")
public class MemberFundRecordAppController {

    @Autowired
    private MemberFundRecordService service;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @ApiResponse
    public Result<PageDate<MemberFundRecordVo>> selectByPage(@Parameter MemberFundRecordVo query, PageParam page) {
        return Result.ok(service.selectByPage(query, page));
    }

    @GetMapping("/list")
    @Operation(summary = "列表查询")
    public Result<List<MemberFundRecord>> selectList(MemberFundRecord query) {
        return Result.ok(service.selectList(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "通过ID查询详情")
    public Result<MemberFundRecord> detail(@PathVariable("id") Long id) {
        return Result.ok(service.detail(id));
    }

    @GetMapping("/count")
    @Operation(summary = "统计基金收益")
    public Result<List<CountMemberFundVo>> countFund(){
        return Result.ok(service.countFund());
    }

    @GetMapping("/sell/{id}")
    @Operation(summary = "基金出售")
    public Result<Boolean> sell(@PathVariable("id") Long id){
        return Result.ok(service.sell(id));
    }

}
