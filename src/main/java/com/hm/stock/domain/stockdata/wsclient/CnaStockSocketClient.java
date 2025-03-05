package com.hm.stock.domain.stockdata.wsclient;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hm.stock.domain.stock.entity.Stock;
import com.hm.stock.domain.stock.mapper.StockMapper;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.utils.ExecuteUtil;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class CnaStockSocketClient extends WebSocketClient {
    private final StockMapper stockMapper;

    public CnaStockSocketClient(String url, Map<String, String> header) {
        super(URI.create(url), header);
        this.stockMapper = SpringUtil.getBean(StockMapper.class);
        log.debug("开始链接，推送这个票 {}", url);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.debug("打开了链接...");
        send("ping");
    }

    @Override
    public void onMessage(String s) {
        log.debug("收到消息 {} ", s);

        if ("pong".equalsIgnoreCase(s)) {
            log.debug("收到服务回复心跳了");
            send("pone");
            return;
        }

        // 0 名字 1最新价 2 涨跌额 3 涨跌幅  4 成交量 5 成交额
        String[] arr = s.split("\n");
        for (String s1 : arr) {
            if (s1.contains("sys_nxkey")) {
                continue;
            }
            ExecuteUtil.STOCK_CNA.execute(() -> {
                updateHis(s1);
            });
        }
    }

    private synchronized void updateHis(String s1) {
        try {
            log.debug("[{}]===> 处理下新数据=>>>  {}", getClass().getSimpleName(), s1);
            String[] hqarr = s1.split(",");
            String[] strings = hqarr[0].split("=");
            String name = strings[1];
            String code = strings[0].replace("s_", "");
            String price = hqarr[1];
            String zde = hqarr[2];
            String zdf = hqarr[3];
            String volumes = hqarr[4];
            String amounts = hqarr[5];
            Stock stockHis = new Stock();
            stockHis.setLast(new BigDecimal(price));
            stockHis.setAmounts(new BigDecimal(amounts).doubleValue() > 0 ? new BigDecimal(amounts).multiply(
                    new BigDecimal(10000)) : new BigDecimal(amounts));
            stockHis.setVolume(new BigDecimal(volumes));
            stockHis.setChgPct(new BigDecimal(zdf));
            stockHis.setChg(new BigDecimal(zde));

            QueryWrapper<Stock> ew = new QueryWrapper<>();
            ew.eq("gid", code);
            Stock stock = stockMapper.selectOne(ew);
            if (LogicUtils.isNull(stock)) {
                return;
            }

            BigDecimal zuix = stock.getLast();
            if (zuix != null && zuix.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal diff = stockHis.getLast().subtract(zuix);
                diff = diff.abs();
                BigDecimal up = stockHis.getLast().multiply(BigDecimal.valueOf(0.3));
                if (diff.compareTo(up) > 0) {
                    log.debug("查出来的票跟数据对不上. 涨跌幅超过百分之30 了{} =====>  {}", stockHis, s1);
                    return;
                }
            }

            stockHis.setId(stock.getId());
            stockMapper.updateById(stockHis);

        } catch (Exception e) {
            log.error("处理股票价格错误了 " + s1, e);
        }
    }


    @Override
    public void onMessage(ByteBuffer bytes) {
        super.onMessage(bytes);
        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
        try {
            CharBuffer decode = decoder.decode(bytes.asReadOnlyBuffer());
            this.onMessage(decode.toString());
        } catch (Exception e) {
            log.error("收到消息出异常了", e);
        }
    }


    @PreDestroy
    public void disconnect() {
        super.close();
    }

    @Override
    public void onClose(int code, String s, boolean b) {
        log.debug("链接关闭了 url=>{} code=>{} err=>{} b=>{}", getURI(), code, s, b);

    }

    @Override
    public void onError(Exception e) {
        log.debug("socker 发生错误了 ", e);
    }

}
