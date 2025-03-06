package com.hm.stock.domain.stock.vo;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class MemberPositionStatVo {


    @Schema(title = "量化交易总资金")
    @Parameter(description = "量化交易总资金")
    private BigDecimal quantificationSum;

    @Schema(title = "可用资金")
    @Parameter(description = "可用资金")
    private BigDecimal enableSum;

    @Schema(title = "占用资金")
    @Parameter(description = "占用资金")
    private BigDecimal occupancySum;

    @Schema(title = "净收益")
    @Parameter(description = "净收益")
    private BigDecimal profitSum;

    @Schema(title = "浮动收益")
    @Parameter(description = "浮动收益")
    private BigDecimal floatingProfitSum;


}
