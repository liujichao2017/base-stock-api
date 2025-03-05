package com.hm.stock.domain.coin;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

/**
 * 接口文档
 * https://huobiapi.github.io/docs/spot/v1/cn/#5ea2e0cde2
 */
@Slf4j
public class HuobiTool {

    private final static RestTemplate restTemplate;

    static {
        restTemplate = new RestTemplate();
    }

    private final static String DOMAIN = "https://api-aws.huobi.pro";

    /**
     * 获取所有币种(V2)
     *
     * @return .
     */
    public static String currencies() {
        return request("/v2/settings/common/currencies");
    }

    /**
     * 获取币种配置
     *
     * @return .
     */
    public static String currencys() {
        return request("/v1/settings/common/currencys");
    }

    /**
     * 获取所有交易对(V2)
     *
     * @return .
     */
    public static String symbols() {
        return request("/v1/settings/common/symbols");
    }

    /**
     * 获取所有交易对(V2)
     *
     * @return .
     */
    public static String marketSymbols() {
        return request("/v1/settings/common/market-symbols");
    }

    /**
     * 获取所有交易对(V2)
     *
     * @return .
     */
    public static String ticker(String symbol) {

        return request("/market/detail/merged?symbol={symbol}", ImmutableMap.of("symbol", symbol));
    }

    public static String historyKline(String symbol, String period, Integer size) {

        return request("/market/history/kline?symbol={symbol}&period={period}&size={size}", ImmutableMap.of("symbol", symbol, "period", period, "size", size));
    }

    public static String historyTrade(String symbol) {

        return request("/market/history/trade?symbol={symbol}&size=40", ImmutableMap.of("symbol", symbol));
    }




    public static String historyDepth(String symbol, Integer depth, String type) {

        return request("/market/depth?symbol={symbol}&depth={depth}&type={type}", ImmutableMap.of("symbol", symbol, "depth", depth, "type", type));
    }

    public static String request(String path) {
        return request(path, null);
    }

    public static String request(String path, Map<String, Object> params) {
        String url = DOMAIN + path;
        String resp;
        if (Objects.isNull(params)) {

            resp = restTemplate.getForObject(url, String.class);
        } else {
            resp = restTemplate.getForObject(url, String.class, params);

        }
        log.debug("发起请求url {}  回复包文 {}", url, resp);
        return resp;

    }

    public static String marketTickers() {
        return request("/market/tickers");
    }
}
