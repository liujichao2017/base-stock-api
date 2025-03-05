package com.hm.stock.domain.stock.controller.app;

import com.hm.stock.domain.stock.entity.StockLevel;
import com.hm.stock.domain.stock.service.StockLevelService;
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
@RequestMapping("/app/stock/level")
@Tag(name = "杠杆配置接口")
public class StockLevelAppController {

    @Autowired
    private StockLevelService service;


    @GetMapping("/list")
    @Operation(summary = "列表查询")
    public Result<List<StockLevel>> selectList(StockLevel query) {
        return Result.ok(service.selectList(query));
    }


}
