package com.hm.stock.domain.coin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.joda.time.LocalDate;

import java.util.concurrent.TimeUnit;

@Getter
@AllArgsConstructor
public enum KlinePeriodEnum {
    //时间阶段: 1min, 5min, 15min, 30min, 60min, 1day, 1week, 1year
    MIN_1("1min", 1, TimeUnit.MINUTES),
    MIN_5("5min", 5, TimeUnit.MINUTES),
    MIN_15("15min", 15, TimeUnit.MINUTES),
    MIN_30("30min", 30, TimeUnit.MINUTES),
    MIN_60("60min", 60, TimeUnit.MINUTES),
    DAY_1("1day", 1, TimeUnit.DAYS),
    WEEK_1("1week", 7, TimeUnit.DAYS),
    YEAR_1("1year", 360, TimeUnit.DAYS),
    ;
    private final String name;
    private final int num;
    private final TimeUnit unit;

    public long toSeconds() {
        if (YEAR_1 == this) {
            return unit.toSeconds(LocalDate.now().getDayOfYear());
        }
        return unit.toSeconds(num);
    }

    public long toMillis() {
        if (YEAR_1 == this) {
            return unit.toSeconds(LocalDate.now().getDayOfYear());
        }
        return unit.toMillis(num);
    }


}
