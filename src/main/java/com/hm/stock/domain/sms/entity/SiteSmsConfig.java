package com.hm.stock.domain.sms.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.String;

/**
* 短信配置 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class SiteSmsConfig extends BaseEntity {

    @Schema(title = "短信类型, 短信的服务商: maco, 字段预留")
    @Parameter(description = "短信类型, 短信的服务商: maco, 字段预留")
    private String type;

    @Schema(title = "国家")
    @Parameter(description = "国家")
    private String country;

    @Schema(title = "区号")
    @Parameter(description = "区号")
    private String areaCode;

    @Schema(title = "短信配置 see type = maco > MacoSmsConfig")
    @Parameter(description = "短信配置 see type = maco > MacoSmsConfig")
    private String json;

    @Schema(title = "启用状态:  see YNEnum, 0: 否. 1: 是 ")
    @Parameter(description = "启用状态:  see YNEnum, 0: 否. 1: 是 ")
    private String status;

}
