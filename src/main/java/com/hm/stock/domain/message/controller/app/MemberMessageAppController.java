package com.hm.stock.domain.message.controller.app;

import com.hm.stock.domain.message.entity.MemberMessage;
import com.hm.stock.domain.message.service.MemberMessageService;
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
@RequestMapping("/app/member/message")
@Tag(name = "用户消息接口")
public class MemberMessageAppController {

    @Autowired
    private MemberMessageService service;


    @GetMapping("/list")
    @Operation(summary = "列表查询")
    public Result<List<MemberMessage>> selectList(MemberMessage query) {
        return Result.ok(service.selectList(query));
    }

    @GetMapping("/countUnread")
    @Operation(summary = "未读数量")
    public Result<Long> countUnread() {
        return Result.ok(service.countUnread());
    }

    @GetMapping("/read")
    @Operation(summary = "阅读全部")
    public Result<Boolean> read() {
        return Result.ok(service.read());
    }


}
