package com.hm.stock.domain.sms.enums;

import com.hm.stock.domain.market.enums.StockDataSourceEnum;
import com.hm.stock.modules.execptions.ErrorResultCode;
import com.hm.stock.modules.execptions.InternalException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SmsTypeEnum {
    MACO("maco"),
    ;
    private final String type;


    public static SmsTypeEnum getEnum(String type) {
        for (SmsTypeEnum e : SmsTypeEnum.values()) {
            if (e.type.equals(type)) {
                return e;
            }
        }
        throw new InternalException(ErrorResultCode.E000001);
    }

}
