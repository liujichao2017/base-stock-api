package com.hm.stock.domain.stock.controller.app;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hm.stock.modules.common.Result;
import com.hm.stock.modules.utils.HttpClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/stock/Klsescreener")
@Tag(name = "马来公司信息")
public class StockKlsescreenerAppController {

    @GetMapping("/info/{code}")
    @Operation(summary = "查询股票信息")
    public JSONObject info(@PathVariable("code") String code) {
        return JSONObject.parseObject(HttpClient.sendGet("http://206.238.199.99:9305/api/reptile/klsescreener/info/" + code));
    }

    @GetMapping("/annual/{code}")
    @Operation(summary = "年度财务报告")
    public JSONObject annual(@PathVariable("code") String code) {
        return JSONObject.parseObject(HttpClient.sendGet("http://206.238.199.99:9305/api/reptile/klsescreener/annual/" + code));
    }

    @GetMapping("/dividends/{code}")
    @Operation(summary = "股息")
    public JSONObject dividends(@PathVariable("code") String code) {
        return JSONObject.parseObject(HttpClient.sendGet("http://206.238.199.99:9305/api/reptile/klsescreener/dividends/" + code));
    }

    @GetMapping("/quarter/reports/{code}")
    @Operation(summary = "季度财务报告")
    public JSONObject quarterReports(@PathVariable("code") String code) {
        return JSONObject.parseObject(HttpClient.sendGet("http://206.238.199.99:9305/api/reptile/klsescreener/quarter/reports/" + code));
    }
}
