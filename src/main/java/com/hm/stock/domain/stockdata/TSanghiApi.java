package com.hm.stock.domain.stockdata;
import java.math.BigDecimal;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hm.stock.domain.stockdata.reset.vo.Kline;
import com.hm.stock.modules.utils.HttpClient;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class TSanghiApi {

    private static final String CNA = "CHN";
    private static final String HK = "HKG";
    private static final String TOKEN = "3d2b47033ef64509a48a6fad09e93f82";
    public static void main(String[] args) {
        System.out.println(getIndexKline(CNA,"399006","D"));
    }

    /**
     * https://tsanghi.com/api/fin/index/{country_code}/1min/realtime?token={token}&ticker={ticker}
     * https://tsanghi.com/api/fin/index/{country_code}/5min/realtime?token={token}&ticker={ticker}
     * https://tsanghi.com/api/fin/index/{country_code}/15min/realtime?token={token}&ticker={ticker}
     * https://tsanghi.com/api/fin/index/{country_code}/30min/realtime?token={token}&ticker={ticker}
     * https://tsanghi.com/api/fin/index/{country_code}/60min/realtime?token={token}&ticker={ticker}
     * https://tsanghi.com/api/fin/index/{country_code}/daily/realtime?token={token}&ticker={ticker}
     * https://tsanghi.com/api/fin/index/{country_code}/weekly/realtime?token={token}&ticker={ticker}
     * https://tsanghi.com/api/fin/index/{country_code}/monthly/realtime?token={token}&ticker={ticker}
     * @param country
     * @param code
     * @param time
     * @return
     */
    public static List<Kline> getIndexKline(String country,String code,String time){
        String url = "https://tsanghi.com/api/fin/index/%s/%s/realtime?token=%s&ticker=%s&columns=amount,ticker,date,open,high,low,close,volume,date";
        url = String.format(url, country, convertTime(time), TOKEN, code);
        log.info("查询沧海K线: {}", url);
        String s = HttpClient.sendGet(url);
        JSONObject res = JSONObject.parseObject(s);
        if (res.getLong("code") != 200L) {
            return Collections.emptyList();
        }
        List<Kline> arr = new ArrayList<>();
        JSONArray data = res.getJSONArray("data");
        for (int i = 0; i < data.size(); i++) {
            JSONObject item = data.getJSONObject(i);
            arr.add(convertKline(item));
        }
        return arr;
    }

    /**
     *          "ticker": "399006",
     *             "date": "2025-01-07 10:40:00",
     *             "open": 2002.667,
     *             "high": 2002.765,
     *             "low": 2002.41,
     *             "close": 2002.603,
     *             "volume": 308730,
     *             "amount": 482923507.61
     * @param item
     * @return
     */
    private static Kline convertKline(JSONObject item) {
        Kline kline = new Kline();
        kline.setT(parseTime(item.getString("date")));
        kline.setC(item.getBigDecimal("close"));
        kline.setO(item.getBigDecimal("open"));
        kline.setH(item.getBigDecimal("high"));
        kline.setL(item.getBigDecimal("low"));
        kline.setV(item.getBigDecimal("amount"));
        kline.setVo(item.getBigDecimal("volume"));
        return kline;
    }

    private static Long parseTime(String date){
        try {

            String pattern = "yyyy-MM-dd HH:mm:ss";
            if (date.length() == "yyyy-MM-dd HH:mm".length()) {
                pattern = "yyyy-MM-dd HH:mm";
            }
            if (date.length() == "yyyy-MM-dd".length()) {
                pattern = "yyyy-MM-dd";
            }

            return new SimpleDateFormat(pattern).parse(date).getTime() / 1000;
        } catch (ParseException e) {
            log.error("时间转换失败: {}", date);
            return System.currentTimeMillis() / 1000;
        }
    }

    private static String convertTime(String time) {
        switch (time) {
            case "D":
                return "daily";
            case "W":
                return "weekly";
            case "M":
                return "monthly";
            default:
                return time + "min";
        }
    }
}
