package com.hm.stock.domain.coin.enums;

import com.hm.stock.modules.execptions.ErrorResultCode;
import com.hm.stock.modules.execptions.InternalException;
import lombok.Getter;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Getter
public enum DeliveryTimeUnitEnum {
    // 时间单位: 1: 秒, 2:分, 3:小时, 4:天, 5:指定到期时间
    SECONDS("1", TimeUnit.SECONDS),
    MINUTES("2", TimeUnit.MINUTES),
    HOURS("3", TimeUnit.HOURS),
    DAYS("4", TimeUnit.DAYS),
    DESIGNATION("5"),
    ;
    private final String unit;
    private TimeUnit timeUnit;

    DeliveryTimeUnitEnum(String unit) {
        this.unit = unit;
    }

    DeliveryTimeUnitEnum(String unit, TimeUnit timeUnit) {
        this.unit = unit;
        this.timeUnit = timeUnit;
    }

    public static DeliveryTimeUnitEnum getEnum(String unit) {
        DeliveryTimeUnitEnum[] values = DeliveryTimeUnitEnum.values();
        for (DeliveryTimeUnitEnum value : values) {
            if (value.getUnit().equals(unit)) {
                return value;
            }
        }
        throw new InternalException(ErrorResultCode.E000001);
    }

    public static Date getDate(String unit, Long time) {
        DeliveryTimeUnitEnum deliveryTimeUnitEnum = getEnum(unit);
        if (DESIGNATION == deliveryTimeUnitEnum) {
            return new Date(time);
        }
        return new Date(System.currentTimeMillis() + deliveryTimeUnitEnum.getTimeUnit().toMillis(time));
    }
}
