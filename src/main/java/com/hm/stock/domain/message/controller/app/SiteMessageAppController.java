package com.hm.stock.domain.message.controller.app;

import com.hm.stock.domain.message.entity.MemberMessage;
import com.hm.stock.domain.message.entity.SiteMessage;
import com.hm.stock.domain.message.service.SiteMessageService;
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
@RequestMapping("/app/site/message")
@Tag(name = "公告接口")
public class SiteMessageAppController {

    @Autowired
    private SiteMessageService service;

    @GetMapping("/list")
    @Operation(summary = "列表查询")
    public Result<List<SiteMessage>> selectList(SiteMessage query) {
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
