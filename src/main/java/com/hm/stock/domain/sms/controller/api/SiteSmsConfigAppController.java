package com.hm.stock.domain.sms.controller.api;

import com.hm.stock.domain.sms.entity.SiteSmsConfig;
import com.hm.stock.domain.sms.service.SiteSmsConfigService;
import com.hm.stock.domain.sms.vo.SendCodeVo;
import com.hm.stock.domain.sms.vo.SendSmsVo;
import com.hm.stock.modules.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sms")
@Tag(name = "短信配置接口")
public class SiteSmsConfigAppController {

    @Autowired
    private SiteSmsConfigService service;

    @GetMapping("/config")
    @Operation(summary = "短信配置列表")
    public Result<List<SiteSmsConfig>> selectList(SiteSmsConfig query) {
        return Result.ok(service.selectList(query));
    }

    @PostMapping("/send")
    @Operation(summary = "发送短信")
    public Result<Boolean> send(@RequestBody @Parameter SendSmsVo body) {
        return Result.ok(service.send(body));
    }

    @PostMapping("/code")
    @Operation(summary = "获取短信验证码")
    public Result<String> code(SendCodeVo body) {
        return Result.ok(service.code(body));
    }

}
