package com.hm.stock.domain.stock.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.hm.stock.domain.market.entity.Market;
import com.hm.stock.domain.market.mapper.MarketMapper;
import com.hm.stock.domain.stock.entity.Stock;
import com.hm.stock.domain.stock.mapper.StockMapper;
import com.hm.stock.domain.stock.service.StockClosedService;
import com.hm.stock.domain.stock.service.StockService;
import com.hm.stock.domain.stockdata.StockApi;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.Result;
import com.hm.stock.modules.execptions.CommonResultCode;
import com.hm.stock.modules.utils.ExecuteUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stock")
@Tag(name = "股票同步接口")
public class StockApiStockController {

    @Autowired
    private MarketMapper marketMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private StockService stockService;

    @Autowired
    private StockClosedService stockClosedService;

    @GetMapping("/holidaycalendar")
    @Operation(summary = "同步节假日")
    public Result<Boolean> syncHolidayCalendar() {
        stockClosedService.syncHolidayCalendar();
        return Result.ok(true);
    }

    @GetMapping("/last/{id}")
    @Operation(summary = "查询股票最新价格")
    public Result<Stock> stockLast(@PathVariable("id") Long id) {
        Stock stock = stockService.getStockByLatest(id);
        return Result.ok(stock);
    }

    @GetMapping("/{id}")
    @Operation(summary = "股票同步")
    public Result stock(@PathVariable("id") Integer id) {
        Market market = marketMapper.selectById(id);
        LogicUtils.assertNotNull(market, CommonResultCode.AUT_ERROR);
        ExecuteUtil.COMMON.execute(() -> {
            StockApi api = StockApi.getInstance(market);
            List<Stock> list = api.getList(market);
            saveStock(list);
        });
        return Result.ok();
    }

    @GetMapping("/index/{id}")
    @Operation(summary = "指数同步")
    public Result index(@PathVariable("id") Integer id) {
        Market market = marketMapper.selectById(id);
        LogicUtils.assertNotNull(market, CommonResultCode.AUT_ERROR);
        ExecuteUtil.COMMON.execute(() -> {
            StockApi api = StockApi.getInstance(market);
            List<Stock> list = api.getIndex(market);
            saveStock(list);
        });
        return Result.ok();
    }

    private void saveStock(List<Stock> list) {
        for (List<Stock> stocks : Lists.partition(list, 1000)) {
            QueryWrapper<Stock> ew = new QueryWrapper<>();
            ew.in("gid", stocks.stream().map(Stock::getGid).collect(Collectors.toList()));
            List<Stock> stocksByDb = stockMapper.selectList(ew);
            if (LogicUtils.isEmpty(stocksByDb)) {
                for (Stock stock : stocks) {
                    stockMapper.insert(stock);
                }
                continue;
            }
            Map<String, Stock> stockByDbMap = stocksByDb.stream().collect(Collectors.toMap(Stock::getGid, s -> s));
            List<Stock> insert = new ArrayList<>();
            List<Stock> update = new ArrayList<>();

            for (Stock stock : stocks) {
                Stock stockByDb = stockByDbMap.get(stock.getGid());
                if (stockByDb == null) {
                    insert.add(stock);
                } else {
                    stock.setId(stockByDb.getId());
                    stock.setName(null);
                    update.add(stock);
                }
            }
            if (LogicUtils.isNotEmpty(insert)) {
                for (Stock stock : insert) {
                    stockMapper.insert(stock);
                }

            }
            if (LogicUtils.isNotEmpty(update)) {
                for (Stock stock : update) {
                    stockMapper.updateById(stock);
                }
            }
        }
    }


}
