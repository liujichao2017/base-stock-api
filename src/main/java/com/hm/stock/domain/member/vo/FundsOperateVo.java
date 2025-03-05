package com.hm.stock.domain.member.vo;

import com.hm.stock.domain.market.entity.Market;
import com.hm.stock.domain.member.entity.Member;
import com.hm.stock.domain.member.enums.FundsOperateTypeEnum;
import com.hm.stock.domain.member.enums.FundsSourceEnum;
import com.hm.stock.modules.utils.JsonUtil;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class FundsOperateVo {
    private FundsSourceEnum source;

    private FundsOperateTypeEnum operateType;

    /**
     * 用户ID与用户对象填一个即可
     */
    private Long memberId;

    private Member member;

    /**
     * 市场对象与市场Id填一个即可
     */
    private Market market;
    private Long marketId;


    private BigDecimal amt;


    /**
     * 是否能扣为负数, 只会扣除可用资金
     */
    private boolean negative = false;

    /**
     * 源记录ID: 由哪条记录操作的资金
     */
    private Long sourceId;
    /**
     * 源记录名称: 股票名称, 基金名称, 新股名称...无则留空
     */
    private String name;
    /**
     * 源记录名称: 股票代码, 基金代码, 新股代码...无则留空
     */
    private String code;

    private final Map<String, Object> fundsInfoMap = new LinkedHashMap<>();

    @Schema(title = "操作的内容存放JSON,根据交易的名称,产品代码,等信息拼接JSON, 用于前端展示, 标准格式定义 name code ")
    @Parameter(description = "操作的内容存放JSON,根据交易的名称,产品代码,等信息拼接JSON, 用于前端展示, 标准格式定义 name code ")
    private String contentJson;


    @Schema(title = "会员可见 see YNEnum, 0: 否(后台上下分). 1: 是(其他)")
    @Parameter(description = "会员可见 see YNEnum, 0: 否(后台上下分). 1: 是(其他)")
    private String visible;

    public FundsOperateVo set(String key, Object value) {
        this.fundsInfoMap.put(key, value);
        return this;
    }

    public void build() {
        this.contentJson = JsonUtil.toStr(fundsInfoMap);
    }

    public static class FundsInfoKey {
        // 股票名称
        public static final String STOCK_NAME = "stockName";
        // 股票代码
        public static final String STOCK_CODE = "stockCode";
        // 产品名称
        public static final String PRODUCT_NAME = "productName";
        // 产品代码
        public static final String PRODUCT_CODE = "productCode";
        // 占用本金
        public static final String OCCUPANCY_AMT = "occupancyAmt";
        // 冻结本金
        public static final String FREEZE_AMT = "freezeAmt";
        // 退还本金
        public static final String ROLLBACK_AMT = "rollbackAmt";
        // 补缴本金
        public static final String BACK_PAYMENT_AMT = "backPaymentAmt";
        // 总盈亏金额
        public static final String ALL_PROFIT_LOSS_AMT = "allProfitLossAmt";
        // 盈亏金额
        public static final String PROFIT_LOSS_AMT = "profitLossAmt";
        // 币名
        public static final String COIN_NAME = "coinName";
        // 手续费
        public static final String FEE_AMT = "feeAmt";

    }
}
