package com.hm.stock.domain.stock.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.String;

/**
* malaysiawarrants 实体类
*/
@Data
public class StockMlMalaysiawarrants {

    @Schema(title = "认股权证名称")
    @Parameter(description = "认股权证名称")
    @TableId
    private String dwSymbol;

    @Schema(title = "SPEE_tc.KL")
    @Parameter(description = "SPEE_tc.KL")
    private String ric;

    @Schema(title = "元数据")
    @Parameter(description = "元数据")
    private String underlyingSymbol;

    @Schema(title = "名称")
    @Parameter(description = "名称")
    private String underlyingName;

    @Schema(title = "SPEE.KL")
    @Parameter(description = "SPEE.KL")
    private String underlyingRic;

    @Schema(title = "类型 C/P")
    @Parameter(description = "类型 C/P")
    private String type;

    @Schema(title = "行使价")
    @Parameter(description = "行使价")
    private String exercisePrice;

    @Schema(title = "行使比率")
    @Parameter(description = "行使比率")
    private String convRatio;

    @Schema(title = "0.200")
    @Parameter(description = "0.200")
    private String dwps;

    @Schema(title = "Macquarie")
    @Parameter(description = "Macquarie")
    private String issuer;

    @Schema(title = "增量(%)")
    @Parameter(description = "增量(%)")
    private String delta;

    @Schema(title = "隐含波动率(%)")
    @Parameter(description = "隐含波动率(%)")
    private String impliedVolalitiy;

    @Schema(title = "敏感度")
    @Parameter(description = "敏感度")
    private String sensitivity;

    @Schema(title = "-0.52")
    @Parameter(description = "-0.52")
    private String theta;

    @Schema(title = "有效杠杆")
    @Parameter(description = "有效杠杆")
    private String effectiveGearing;

    @Schema(title = "28 Apr 25")
    @Parameter(description = "28 Apr 25")
    private String ltDate;

    @Schema(title = "OTM")
    @Parameter(description = "OTM")
    private String moneyness;

    @Schema(title = "4")
    @Parameter(description = "4")
    private String moneynessPercent;

    @Schema(title = "4% OTM")
    @Parameter(description = "4% OTM")
    private String moneynessC;

    @Schema(title = "买入量")
    @Parameter(description = "买入量")
    private String bidVolume;

    @Schema(title = "311")
    @Parameter(description = "311")
    private String bidVolumeF;

    @Schema(title = "买入价")
    @Parameter(description = "买入价")
    private String bidPrice;

    @Schema(title = "0.040")
    @Parameter(description = "0.040")
    private String bidPriceF;

    @Schema(title = "价格变化(%)")
    @Parameter(description = "价格变化(%)")
    private String priceChange;

    @Schema(title = "-11.1")
    @Parameter(description = "-11.1")
    private String priceChangeF;

    @Schema(title = "卖价")
    @Parameter(description = "卖价")
    private String askPrice;

    @Schema(title = "0.045")
    @Parameter(description = "0.045")
    private String askPriceF;

    @Schema(title = "卖出量")
    @Parameter(description = "卖出量")
    private String askVolume;

    @Schema(title = "85")
    @Parameter(description = "85")
    private String askVolumeF;

    @Schema(title = "交易量")
    @Parameter(description = "交易量")
    private String tradeVolume;

    @Schema(title = "4")
    @Parameter(description = "4")
    private String tradeVolumeF;

    @Schema(title = "最高流动性")
    @Parameter(description = "最高流动性")
    private String highresp;

    @Schema(title = "低时间衰减")
    @Parameter(description = "低时间衰减")
    private String lowtimedecay;

    @Schema(title = "17 Sep 24")
    @Parameter(description = "17 Sep 24")
    private String listDate;

    @Schema(title = "false")
    @Parameter(description = "false")
    private String appchexpry;

    @Schema(title = "新授权")
    @Parameter(description = "新授权")
    private String newwarrant;

    @Schema(title = "库存售罄")
    @Parameter(description = "库存售罄")
    private String soldout;

    @Schema(title = "最快响应")
    @Parameter(description = "最快响应")
    private String moreresponsive;

    @Schema(title = "到期")
    @Parameter(description = "到期")
    private String maturity;

    @Schema(title = "5326CE")
    @Parameter(description = "5326CE")
    private String ticker;

    @Schema(title = "基础价格")
    @Parameter(description = "基础价格")
    private String underlyingPrice;

    @Schema(title = "99SMART-CE")
    @Parameter(description = "99SMART-CE")
    private String dsplyNmll;

    @Schema(title = "高效杠杆")
    @Parameter(description = "高效杠杆")
    private String higheffectivegearing;

}
