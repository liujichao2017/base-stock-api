package com.hm.stock.domain.site.controller.app;

import com.hm.stock.domain.site.entity.DataConfig;
import com.hm.stock.domain.site.service.DataConfigService;
import com.hm.stock.domain.sms.entity.SiteSmsConfig;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/app/data/config")
@Tag(name = "通用配置表接口")
public class DataConfigAppController {

    @Autowired
    private DataConfigService service;

    @GetMapping("/list")
    @Operation(summary = "通用配置-列表")
    public Result<List<DataConfig>> selectList(DataConfig config) {
        return Result.ok(service.selectList(config));
    }

    @GetMapping("/map")
    @Operation(summary = "通用配置-对象")
    public Result<Map<String,String>> selectObj(DataConfig config) {
        Map<String, String> map = new HashMap<>();
        for (DataConfig dataConfig : service.selectList(config)) {
            map.put(dataConfig.getKey(), dataConfig.getVal());
        }
        return Result.ok(map);
    }
}
