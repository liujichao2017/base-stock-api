package com.hm.stock.domain.coin.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.Long;

/**
* 虚拟币杠杆 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class CoinLevel extends BaseEntity {

    @Schema(title = "杠杆倍数")
    @Parameter(description = "杠杆倍数")
    private Long level;

    @Schema(title = "启用: 1: 启用,0: 不启用")
    @Parameter(description = "启用: 1: 启用,0: 不启用")
    private Long status;

}
