package com.hm.stock.domain.market.enums;

import com.hm.stock.modules.execptions.ErrorResultCode;
import com.hm.stock.modules.execptions.InternalException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StockDataSourceEnum {
    /**
     * 匠山数据源
     */
    JS("JS"),
    /**
     * 路透交易所
     */
    LT("LT"),
    /**
     * A股
     */
    CNA("CNA"),
    /**
     * HK
     */
    HK("HK"),
    /**
     * 虚拟币-火币
     */
    COIN("COIN"),
    /**
     * 启源
     */
    QY("QY"),
    ;
    private final String type;

    public static StockDataSourceEnum getEnum(String type) {
        for (StockDataSourceEnum e : StockDataSourceEnum.values()) {
            if (e.type.equals(type)) {
                return e;
            }
        }
        throw new InternalException(ErrorResultCode.E000001);
    }

    public static boolean isCoin(String dataSourceMark) {
        StockDataSourceEnum stockDataSourceEnum = getEnum(dataSourceMark);
        return stockDataSourceEnum == StockDataSourceEnum.COIN;
    }

    public static boolean isStock(String dataSourceMark) {
        StockDataSourceEnum stockDataSourceEnum = getEnum(dataSourceMark);
        return stockDataSourceEnum != StockDataSourceEnum.COIN;
    }


    public boolean isStockHis(){
        return StockDataSourceEnum.CNA == this ;
    }
}
