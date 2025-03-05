package com.hm.stock.domain.news.enums;

import com.hm.stock.modules.execptions.ErrorResultCode;
import com.hm.stock.modules.execptions.InternalException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NewsTypeEnum {
    JS("JS"),
    MY_v1("MY_v1"),
    ;
    private final String type;

    public static NewsTypeEnum getEnum(String type){
        for (NewsTypeEnum value : values()) {
            if (value.getType().equalsIgnoreCase(type)){
                return value;
            }
        }
        throw new InternalException(ErrorResultCode.E000001);
    }
}
