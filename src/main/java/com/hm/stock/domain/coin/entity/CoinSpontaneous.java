package com.hm.stock.domain.coin.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.Long;
import java.lang.String;

/**
*  实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class CoinSpontaneous extends BaseEntity {

    @Schema(title = "交易对")
    @Parameter(description = "交易对")
    private String symbol;

    @Schema(title = "基础币种显示名称")
    @Parameter(description = "基础币种显示名称")
    private String bcdn;

    @Schema(title = "计价币种显示名称")
    @Parameter(description = "计价币种显示名称")
    private String qcdn;

    @Schema(title = "下次执行时间")
    @Parameter(description = "下次执行时间")
    private Long next;

    @Schema(title = "机器人状态1:启用,0:禁用")
    @Parameter(description = "机器人状态1:启用,0:禁用")
    private String status;

}
