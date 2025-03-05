package com.hm.stock.domain.news.controller.app;

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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/news")
@Tag(name = "新闻资讯接口")
public class SiteNewsAppController {

    @Autowired
    private SiteNewsService service;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @ApiResponse
    public Result<PageDate<SiteNews>> selectByPage(@Parameter SiteNews query,@Parameter PageParam page) {
        return Result.ok(service.selectByPage(query, page));
    }
}
