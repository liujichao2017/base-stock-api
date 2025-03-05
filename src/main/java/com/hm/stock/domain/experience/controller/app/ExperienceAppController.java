package com.hm.stock.domain.experience.controller.app;

import com.hm.stock.domain.dailylimit.entity.StockDailyLimit;
import com.hm.stock.domain.dailylimit.service.StockDailyLimitService;
import com.hm.stock.domain.dailylimit.vo.DailyLimitVo;
import com.hm.stock.domain.experience.entity.Experience;
import com.hm.stock.domain.experience.service.ExperienceService;
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
@RequestMapping("/app/rxperience")
@Tag(name = "体验金")
public class ExperienceAppController {

    @Autowired
    private ExperienceService service;


    @GetMapping("/list/{marketId}")
    @Operation(summary = "列表查询")
    public Result<List<Experience>> selectList(@PathVariable("marketId") Long marketId) {
        return Result.ok(service.selectList(marketId));
    }
}
