package com.hm.stock.domain.stockdata;

import com.alibaba.fastjson.JSONObject;
import com.hm.stock.domain.market.entity.Market;
import com.hm.stock.domain.market.enums.StockDataSourceEnum;
import com.hm.stock.domain.stock.entity.Stock;
import com.hm.stock.domain.stockdata.reset.*;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.execptions.ErrorResultCode;
import com.hm.stock.modules.utils.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

public interface StockApi {

    Logger log = LoggerFactory.getLogger(StockApi.class);

    void init(Market market);

    List<Stock> getList(Market market);

    List<Stock> getIndex(Market market);

    Stock getStock(Market market, String gid);

    /**
     * A股不能平凡调用采用延迟加载
     *
     * @param market
     * @param gid
     * @return
     */
    Stock getStockByWs(Market market, Stock stock);

    List<Stock> getStocks(Market market, List<String> gids);

    String getKline(Market market, String gid, String time);

    static StockApi getInstance(Market market) {
        StockApi stockApi = null;
        switch (StockDataSourceEnum.getEnum(market.getDataSourceMark())) {
            case JS:
                stockApi = BeanUtil.getBean(JsStockApi.class);
                break;
            case LT:
                stockApi = BeanUtil.getBean(LtStockApi.class);
                break;
            case CNA:
                stockApi = BeanUtil.getBean(CnAStockApi.class);
                break;
            case HK:
                stockApi = BeanUtil.getBean(HkStockApi.class);
                break;
            case QY:
                stockApi = BeanUtil.getBean(QyStockApi.class);
                break;
        }
        LogicUtils.assertNotNull(stockApi, ErrorResultCode.E000001);
        stockApi.init(market);
        return stockApi;


    }

    static BigDecimal getBigDecimal(JSONObject result, String... keys) {
        for (String key : keys) {
            String val = result.getString(key);
            if (val == null || val.isEmpty()) {
                continue;
            }
            val = "1e100".equals(val) ? "0" : val;
            val = val.replace("+", "");
            val = val.replace("%", "");
            val = val.replace(",", "");
            try {
                return new BigDecimal(val);
            } catch (Exception e) {
                log.error("股票价格解析错误: key:{}, data:{}", key, result);
                return null;
            }
        }
        return null;
    }
}
