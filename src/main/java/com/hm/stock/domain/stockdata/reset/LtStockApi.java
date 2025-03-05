package com.hm.stock.domain.stockdata.reset;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hm.stock.domain.market.entity.Market;
import com.hm.stock.domain.stock.entity.Stock;
import com.hm.stock.domain.stock.entity.StockMlMalaysiawarrants;
import com.hm.stock.domain.stock.mapper.StockMlMalaysiawarrantsMapper;
import com.hm.stock.domain.stockdata.StockApi;
import com.hm.stock.domain.stockdata.reset.vo.Kline;
import com.hm.stock.domain.stockdata.reset.vo.LtConfig;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.enums.YNEnum;
import com.hm.stock.modules.utils.DateTimeUtil;
import com.hm.stock.modules.utils.ExecuteUtil;
import com.hm.stock.modules.utils.HttpClient;
import com.hm.stock.modules.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 路透数据
 */
@Component
public class LtStockApi implements StockApi {
    private final ThreadLocal<LtConfig> threadLocal = new ThreadLocal<>();

    @Override
    public void init(Market market) {
        threadLocal.set(JsonUtil.toObj(market.getDataSourceJson(), LtConfig.class));
    }

    @Override
    public List<Stock> getList(Market market) {
        LtConfig ltConfig = threadLocal.get();
        String res = HttpClient.sendGet(ltConfig.getStockDomain() + "/stock/" + ltConfig.getCountryId());
        if (LogicUtils.isBlank(res)) {
            return Collections.emptyList();
        }
        JSONArray arr = JSONArray.parseArray(res);
        if (arr.isEmpty()) {
            return Collections.emptyList();
        }
        List<Stock> list = new ArrayList<>();
        for (int i1 = 0; i1 < arr.size(); i1++) {
            JSONObject result = arr.getJSONObject(i1);
            Stock stock = buildStock(result);
            stock.setType("1");
            stock.setMarketId(market.getId());
            list.add(stock);
        }
        if (LogicUtils.isNotBlank(ltConfig.getMalaysiawarrants()) && YNEnum.yes(ltConfig.getMalaysiawarrants())) {
            ExecuteUtil.STOCK.execute(() -> {
                syncMalaysiawarrants();
            });
        }
        threadLocal.remove();
        return list;
    }

    private void syncMalaysiawarrants() {
        String url = "http://206.238.199.99:9305/api/reptile/malaysiawarrants/page/";
        String res = HttpClient.sendGet(url + 1);
        JSONObject obj = JSONObject.parseObject(res);
        JSONObject data = obj.getJSONObject("data");
        Long total = data.getLong("total");

        JSONArray list = data.getJSONArray("list");
        List<StockMlMalaysiawarrants> javaList = list.toJavaList(StockMlMalaysiawarrants.class);
        for (StockMlMalaysiawarrants malaysiawarrants : javaList) {
            StockMlMalaysiawarrantsMapper mapper = SpringUtil.getBean(StockMlMalaysiawarrantsMapper.class);
            try {
                mapper.insert(malaysiawarrants);
            } catch (Exception e) {
                mapper.updateById(malaysiawarrants);
            }
        }
        int size = (int) ((total / 1000) + (total % 1000 == 0 ? 0 : 1));
        for (int i = 2; i <= size; i++) {
            res = HttpClient.sendGet(url + i);
            obj = JSONObject.parseObject(res);
            data = obj.getJSONObject("data");
            list = data.getJSONArray("list");
            javaList = list.toJavaList(StockMlMalaysiawarrants.class);
            for (StockMlMalaysiawarrants malaysiawarrants : javaList) {
                StockMlMalaysiawarrantsMapper mapper = SpringUtil.getBean(StockMlMalaysiawarrantsMapper.class);
                try {
                    mapper.insert(malaysiawarrants);
                } catch (Exception e) {
                    mapper.updateById(malaysiawarrants);
                }
            }
        }
    }

    @Override
    public List<Stock> getIndex(Market market) {
        LtConfig ltConfig = threadLocal.get();
        String res = HttpClient.sendGet(ltConfig.getStockDomain() + "/stock/index/" + ltConfig.getCountryId());
        if (LogicUtils.isBlank(res)) {
            return Collections.emptyList();
        }
        JSONArray arr = JSONArray.parseArray(res);
        if (arr.isEmpty()) {
            return Collections.emptyList();
        }
        List<Stock> list = new ArrayList<>();
        for (int i1 = 0; i1 < arr.size(); i1++) {
            JSONObject result = arr.getJSONObject(i1);
            Stock stock = buildStock(result);
            stock.setType("2");
            stock.setMarketId(market.getId());
            list.add(stock);
        }
        threadLocal.remove();
        return list;
    }

    @Override
    public Stock getStock(Market market, String gid) {
        LtConfig ltConfig = threadLocal.get();
        String res = HttpClient.sendGet(ltConfig.getStockDomain() + "/stock/" + ltConfig.getCountryId() + "/" + gid);
        if (res == null || res.isEmpty()) {
            return null;
        }
        JSONObject result = JSONObject.parseObject(res);
        threadLocal.remove();
        return buildStock(result);
    }

    @Override
    public List<Stock> getStocks(Market market, List<String> gids) {
        return gids.stream().map(gid -> getStock(market, gid)).collect(Collectors.toList());
    }

    @Override
    public String getKline(Market market, String gid, String time) {
        LtConfig ltConfig = threadLocal.get();
//        String res = "";
//        switch (time) {
//            case "D":
//                res = getKlineByHistorical(gid, "P1D", "672");
//                break;
//            case "W":
//                res = getKlineByHistorical(gid, "P1W", "96");
//                break;
//            case "M":
//                res = getKlineByHistorical(gid, "P1M", "24");
//                break;
//            default:
//                res = getKlineByRealtime(gid, String.format("PT%sM", time));
//                break;
//        }
//        List<Kline> klines = buildKline(market, res, time);

//        String res = HttpClient.sendGet(String.format("https://history.stocskapis.com/history/kline/%s/%s", gid, time));
//        List<Kline> klines = JSONObject.parseArray(res, Kline.class);
//        if (LogicUtils.isNumber(time) && LogicUtils.isNotEmpty(klines)) {
//            klines = convert(market.getTransAmBegin(), market.getTransPmEnd(), time, klines);
//        } else {
//
//            for (int i = 1; i < klines.size(); i++) {
//                Kline kline1 = klines.get(i - 1);
//                Kline kline2 = klines.get(i);
//                supplement(kline1, kline2);
//            }
//        }
//
//        if (LogicUtils.isNotEmpty(klines) && LogicUtils.isAny(time, "W", "M")) {
//            if (klines.get(klines.size() - 1).getT() > (System.currentTimeMillis() / 1000)) {
//                Long now = getNowTime(gid, "D").getT();
//                klines.get(klines.size() - 1).setT(now);
//            }
//        }
//
//        if (LogicUtils.isNotEmpty(klines) && LogicUtils.isAny(time, "D")) {
//            Kline kline = getNowTime(gid, "1");
//            String t = new SimpleDateFormat("yyyy-MM-dd").format(new Date(kline.getT() * 1000));
//            String d = new SimpleDateFormat("yyyy-MM-dd").format(new Date(klines.get(klines.size() - 1).getT() * 1000));
//
//            if (t.compareTo(d) > 0) {
//                QueryWrapper<Stock> ew = new QueryWrapper<>();
//                ew.eq("gid", gid);
//                Stock stock = SpringUtil.getBean(StockMapper.class).selectOne(ew);
//                if (stock != null) {
//                    kline.setO(stock.getOpen());
//                    kline.setC(stock.getLast());
//                    kline.setH(stock.getHigh());
//                    kline.setL(stock.getLast());
//                }
//                klines.add(kline);
//            }
//        }
//
//        if (LogicUtils.isEmpty(klines) && LogicUtils.isAny(time, "D", "W", "M")) {
//            Kline kline = getNowTime(gid, "1");
//            QueryWrapper<Stock> ew = new QueryWrapper<>();
//            ew.eq("gid", gid);
//            Stock stock = SpringUtil.getBean(StockMapper.class).selectOne(ew);
//            if (stock != null) {
//                kline.setO(stock.getOpen());
//                kline.setC(stock.getLast());
//                kline.setH(stock.getHigh());
//                kline.setL(stock.getLast());
//            }
//            klines.add(kline);
//        }

        return HttpClient.sendGet(
                String.format("https://history.stocskapis.com/history/kline/%s/%s/%s", ltConfig.getCountryId(), gid,
                              time));
    }


    private Kline getNowTime(String gid, String time) {
        String res = HttpClient.sendGet(String.format("https://history.stocskapis.com/history/kline/%s/%s", gid, time));
        List<Kline> klines = JSONObject.parseArray(res, Kline.class);
        return klines.get(klines.size() - 1);
    }

    @Override
    public Stock getStockByWs(Market market, Stock stock) {
        return getStock(market, stock.getGid());
    }

    public static List<Kline> buildKline(Market market, String res, String time) {
        JSONObject json = JSONObject.parseArray(res).getJSONObject(0);
        Map<String, Integer> locationMap = buildLocation(json);
        JSONArray data = json.getJSONArray("data");
        List<Kline> list = new ArrayList<>();
        if (data.isEmpty()) {
            return list;
        }
        for (int i = 0; i < data.size(); i++) {
            JSONArray arr = data.getJSONArray(i);
            Kline kline = new Kline();

            String tStr = arr.getString(locationMap.get("t"));
            kline.setT(parseTime(tStr, DateTimeUtil.getTimestamp(market.getTimeZone())));
            kline.setC(getBigDecimalVal("c", arr, locationMap));
            kline.setO(getBigDecimalVal("o", arr, locationMap));
            kline.setH(getBigDecimalVal("h", arr, locationMap));
            kline.setL(getBigDecimalVal("l", arr, locationMap));
            kline.setV(getBigDecimalVal("v", arr, locationMap));
            kline.setVo(getBigDecimalVal("vo", arr, locationMap));
            list.add(kline);

        }
        list.sort((k1, k2) -> Math.toIntExact(k1.getT() - k2.getT()));

        if (list.size() > 500) {
            return list.subList(list.size() - 500, list.size());
        }
        return list;

    }

    public static void main(String[] args) {

        String gid = "ARKA.KL";
        String time = "1";
        String end = "";
        String res = HttpClient.sendGet(String.format("https://history.stocskapis.com/history/kline/%s/%s", gid, time));
        List<Kline> klines = JSONObject.parseArray(res, Kline.class);
        System.out.println(klines.size());
        if (LogicUtils.isNumber(time) && LogicUtils.isNotEmpty(klines)) {
            klines = convert("09:00", "17:00", time, klines);
        } else {

            for (int i = 1; i < klines.size(); i++) {
                Kline kline1 = klines.get(i - 1);
                Kline kline2 = klines.get(i);
                supplement(kline1, kline2);
            }
        }

        if (LogicUtils.isNotEmpty(klines) && LogicUtils.isAny(time, "D")) {
            res = HttpClient.sendGet(String.format("https://history.stocskapis.com/history/kline/%s/%s", gid, "1"));
            List<Kline> list = JSONObject.parseArray(res, Kline.class);
            Kline kline = list.get(list.size() - 1);
            String t = new SimpleDateFormat("yyyy-MM-dd").format(new Date(kline.getT() * 1000));
            String d = new SimpleDateFormat("yyyy-MM-dd").format(new Date(klines.get(klines.size() - 1).getT() * 1000));

            if (t.compareTo(d) > 0) {
                klines.add(kline);
            }
        }

        System.out.println(klines.size());
        for (Kline kline : klines) {
            System.out.print(formatTest(kline.getT()) + "\t");
            System.out.print(kline.getT() + "\t");
            System.out.print(kline.getO() + "\t");
            System.out.print(kline.getC() + "\t");
            System.out.print(kline.getH() + "\t");
            System.out.print(kline.getL() + "\t");
            System.out.println();
        }
    }

    private static String formatTest(Long aLong) {
        String format = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(aLong * 1000));
        return format;
    }

    private static List<Kline> convert(String start, String endStr, String time, List<Kline> list) {
        int c = Integer.parseInt(time) * 60;
        Kline kline1 = list.get(0);
        kline1.setT(kline1.getT() - (kline1.getT() % c));
        List<Kline> list1 = null;
        for (int i = 1; i < list.size(); i++) {
            kline1 = list.get(i - 1);
            Kline kline2 = list.get(i);
            kline2.setT(kline2.getT() - (kline2.getT() % c));
//            if (checkTime1(kline2.getT())) {
//                list.remove(i);
//                i--;
//                continue;
//            }
            supplement(kline1, kline2);
            if (kline1.getT() < kline2.getT() && kline1.getT() + c != kline2.getT()) {
                Kline kline = new Kline();
                BeanUtils.copyProperties(kline1, kline);
                kline.setT(kline1.getT() + c);
                list.add(i, kline);
                i--;
            }
            if (checkTime(endStr, list.get(i).getT())) {
                list1 = list.subList(0, i);
                break;
            }
        }
        list = list1 != null ? list1 : list;
        list = list.size() > 1000 ? list.subList(list.size() - 1000, list.size()) : list;
        for (int i = 0; i < list.size(); i++) {
            if (!checkTime2(start, endStr, list.get(i).getT())) {
                list.remove(i);
                i--;
            }
        }
        if (LogicUtils.isNotEmpty(list)) {
            Kline kline = list.get(list.size() - 1);
            Long lastT = kline.getT();
            String t = new SimpleDateFormat("HH:mm").format(new Date(lastT * 1000));
            String n = new SimpleDateFormat("HH:mm").format(new Date());
            while (t.compareTo(n) <= 0 && t.compareTo(endStr) <= 0) {
                Kline kline2 = new Kline();
                BeanUtils.copyProperties(kline, kline2);
                kline2.setT(lastT + c);
                list.add(kline2);
                lastT = kline2.getT();
                t = new SimpleDateFormat("HH:mm").format(new Date(lastT * 1000));
            }
        }
        return list;
    }

    private static void supplement(Kline kline1, Kline kline2) {
        kline2.setO(
                kline2.getO() == null || BigDecimal.ZERO.compareTo(kline2.getO()) == 0 ? kline1.getO() : kline2.getO());
        kline2.setC(
                kline2.getC() == null || BigDecimal.ZERO.compareTo(kline2.getC()) == 0 ? kline1.getC() : kline2.getC());
        kline2.setH(
                kline2.getH() == null || BigDecimal.ZERO.compareTo(kline2.getH()) == 0 ? kline1.getH() : kline2.getH());
        kline2.setL(
                kline2.getL() == null || BigDecimal.ZERO.compareTo(kline2.getL()) == 0 ? kline1.getL() : kline2.getL());

    }

    private static boolean checkTime2(String start, String endStr, Long t) {
        String now = new SimpleDateFormat("HH:mm").format(new Date(t * 1000));
        return now.compareTo(start) >= 0 && now.compareTo(endStr) <= 0;
    }

    private static boolean checkTime1(Long t) {
        String now = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String time = new SimpleDateFormat("yyyy-MM-dd").format(new Date(t * 1000));

        return !time.equals(now);
    }

    private static boolean checkTime(String endStr, Long t) {
        String end = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " " + endStr;
//        String end = "2025-01-06" + " " + endStr;
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(t * 1000));
        return time.compareTo(end) > 0;
    }

    private static int getEnd(String endStr) {
        String[] split = endStr.split(":");
        int h = Integer.parseInt(split[0]);
        int m = Integer.parseInt(split[1]);
        int end = (h * 60 * 60) + (m * 60);
        return end;
    }

    public static List<Kline> buildKline(Market market, String res) {
        JSONObject json = JSONObject.parseArray(res).getJSONObject(0);
        Map<String, Integer> locationMap = buildLocation(json);
        JSONArray data = json.getJSONArray("data");
        List<Kline> list = new ArrayList<>();
        if (data.isEmpty()) {
            return list;
        }
        for (int i = 0; i < data.size(); i++) {
            JSONArray arr = data.getJSONArray(i);
            Kline kline = new Kline();

            String tStr = arr.getString(locationMap.get("t"));
            kline.setT(parseTime(tStr, DateTimeUtil.getTimestamp(market.getTimeZone())));

            kline.setC(getBigDecimalVal("c", arr, locationMap));
            kline.setO(getBigDecimalVal("o", arr, locationMap));
            kline.setH(getBigDecimalVal("h", arr, locationMap));
            kline.setL(getBigDecimalVal("l", arr, locationMap));
            kline.setV(getBigDecimalVal("v", arr, locationMap));
            kline.setVo(getBigDecimalVal("vo", arr, locationMap));
            list.add(kline);

        }
        list.sort((k1, k2) -> Math.toIntExact(k1.getT() - k2.getT()));
        return list;

    }

    private static Map<String, Integer> buildLocation(JSONObject json) {
        Map<String, Integer> map = new HashMap<>();
        // 时间
        Integer dateLocation = getLocation(json, "DATE_TIME");
        if (dateLocation == null) {
            dateLocation = getLocation(json, "DATE");
        }
        map.put("t", dateLocation);
        // 今日开盘价今日开盘价或价值。此字段的来源取决于市场和工具类型。
        map.put("o", getLocation(json, "OPEN_PRC"));
        //
        map.put("c", getLocation(json, "TRDPRC_1"));
        // 今日最高价今日最高交易价值。
        map.put("h", getLocation(json, "HIGH_1"));
        // 今日最低价今日最低交易价值。
        map.put("l", getLocation(json, "LOW_1"));

        // 特定工具的未缩放成交价值（市场日内所有交易价值的总和）。交易量*价格的总和
        map.put("v", getLocation(json, "TRNOVR_UNS"));
        // 根据市场惯例未标定的累计成交股数、手数或合约数
        map.put("vo", getLocation(json, "ACVOL_UNS"));

        return map;
    }

    public static Integer getLocation(JSONObject json, String key) {
        JSONArray headers = json.getJSONArray("headers");
        int i = 0;
        for (; i < headers.size(); i++) {
            JSONObject header = headers.getJSONObject(i);
            if (key.equals(header.getString("name"))) {
                return i;
            }
        }
        return null;
    }

    private static BigDecimal getBigDecimalVal(String key, JSONArray arr, Map<String, Integer> locationMap) {
        Integer location = locationMap.get(key);
        if (location == null) {
            return BigDecimal.ZERO;
        }
        return arr.getBigDecimal(location);
    }


    public static Long parseTime(String time, int timestamp) {
        try {

            String pattern = "yyyy-MM-dd'T'hh:mm:ss";
            if (time.length() == 10) {
                pattern = "yyyy-MM-dd";
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            Date parse = simpleDateFormat.parse(time);
            return (parse.getTime() / 1000) + timestamp;
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static String getKlineByRealtime(String code, String time) {
        String res = HttpClient.sendGet(
                String.format("%s/history/kline/realtime?symbol=%s&type=%s", "https://history.stocskapis.com", code,
                              time));
        if (LogicUtils.isBlank(res)) {
            return "{}";
        }
        return res;
    }

    private static String getKlineByHistorical(String code, String time, String count) {

        String res = HttpClient.sendGet(String.format("%s/history/kline/historical?symbol=%s&type=%s&count=%s",
                                                      "https://history.stocskapis.com", code, time, count));
        if (LogicUtils.isBlank(res)) {
            return "{}";
        }
        return res;
    }

    private Stock buildStock(JSONObject result) {
        Stock stock = new Stock();
        stock.setName(result.getString("name"));
        stock.setCode(StringUtils.isBlank(result.getString("symbol")) ? result.getString("code") : result.getString(
                "symbol"));
        stock.setGid(result.getString("code"));
        stock.setExchanges(getExchanges(stock.getGid()));

        stock.setLast(StockApi.getBigDecimal(result, "last"));
        stock.setChg(StockApi.getBigDecimal(result, "change"));
        stock.setChgPct(StockApi.getBigDecimal(result, "chg"));

        stock.setHigh(StockApi.getBigDecimal(result, "high"));
        stock.setLow(StockApi.getBigDecimal(result, "low"));
        stock.setOpen(StockApi.getBigDecimal(result, "open"));
        stock.setClose(StockApi.getBigDecimal(result, "perClose"));

        stock.setVolume(StockApi.getBigDecimal(result, "volumes"));
        stock.setAmounts(StockApi.getBigDecimal(result, "amounts"));
        return stock;
    }

    private String getExchanges(String gid) {
        if (LogicUtils.isBlank(gid) || !gid.contains(".")) {
            return threadLocal.get().getCountryId();
        }
        try {
            return gid.substring(gid.indexOf(".") + 1);
        } catch (Exception e) {
            log.error("解析交易所失败: {}", gid, e);
            return threadLocal.get().getCountryId();
        }
    }

}
