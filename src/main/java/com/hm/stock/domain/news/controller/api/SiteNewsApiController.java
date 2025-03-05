package com.hm.stock.domain.news.controller.api;

import com.hm.stock.domain.news.entity.SiteNews;
import com.hm.stock.domain.news.service.SiteNewsService;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@Tag(name = "新闻资讯接口")
public class SiteNewsApiController {

    @Autowired
    private SiteNewsService service;

    @GetMapping("/sync")
    @Operation(summary = "分页查询")
    @ApiResponse
    public Result<Boolean> sync() {
        return Result.ok(service.sync());
    }

}
