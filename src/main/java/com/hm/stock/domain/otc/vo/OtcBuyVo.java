package com.hm.stock.domain.otc.vo;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class OtcBuyVo {


    @Schema(title = "大宗ID")
    @Parameter(description = "大宗ID")
    private Long id;

    @Schema(title = "申购数量")
    @Parameter(description = "申购数量")
    private Long nums;

    @Schema(title = "购买方向: 1, 买涨. 2. 买跌, 默认买张")
    @Parameter(description = "购买方向: 1, 买涨. 2. 买跌, 默认买张")
    private String direction;

    @Schema(title = "杠杆, 默认1倍杠杆")
    @Parameter(description = "杠杆, 默认1倍杠杆")
    private Long lever;


}
