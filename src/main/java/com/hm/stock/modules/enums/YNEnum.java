package com.hm.stock.modules.enums;

import com.hm.stock.modules.execptions.ErrorResultCode;
import com.hm.stock.modules.execptions.InternalException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum YNEnum {
    YES("1"),
    NO("0"),
    ;
    private final String type;

    public static YNEnum getEnum(String type) {
        for (YNEnum e : YNEnum.values()) {
            if (e.type.equals(type)) {
                return e;
            }
        }
        throw new InternalException(ErrorResultCode.E000001);
    }

    public static boolean yes(String type) {
        return getEnum(type) == YNEnum.YES;
    }
    public static boolean no(String type) {
        return getEnum(type) == YNEnum.NO;
    }
}
