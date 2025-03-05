package com.hm.stock.domain.member.enums;

import com.hm.stock.modules.execptions.ErrorResultCode;
import com.hm.stock.modules.execptions.InternalException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FundsSourceEnum {
    /**
     * 管理员上分
     */
    ADMIN_AMT("1","0","管理员上分"),
    /**
     * 充值
     */
    RECHARGE("2","0","充值"),
    /**
     * 提现
     */
    WITHDRAW("3","0","提现"),
    /**
     * 货币兑换
     */
    CONVERT("4","1","货币兑换"),
    /**
     * 股票购买
     */
    STOCK("5","1","自主购买"),
    /**
     * 新股
     */
    IPO("6","1","新股"),
    /**
     * 新股配售
     */
    IPO_SALE("7","1","新股配售"),
    /**
     * 大宗
     */
    OTC("8","1","大宗"),
    /**
     * 基金
     */
    FUND("9","1","基金"),
    /**
     * 贷款
     */
    LOAN("10","1","贷款"),
    /**
     * Ai交易
     */
    AI("11","1","AI交易"),
    /**
     * 日内交易
     */
    WITH_DAYS("12","1","日内交易"),
    /**
     * 抢筹
     */
    DAILY_LIMIT("13","1","抢筹"),
    /**
     * 借劵
     */
    BORROW("14","1","借劵"),
    /**
     * U本位虚拟币
     */
    U_COIN("15","1","U本位虚拟币"),
    ;
    private final String type;
    /**
     * 用户可见
     */
    private final String visible;
    private final String name;

    public static FundsSourceEnum getEnum(String type) {
        for (FundsSourceEnum f : FundsSourceEnum.values()) {
            if (f.getType().equals(type)) {
                return f;
            }
        }
        throw new InternalException(ErrorResultCode.E000001);
    }
}
