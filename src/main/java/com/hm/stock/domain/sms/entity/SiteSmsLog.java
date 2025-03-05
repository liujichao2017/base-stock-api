package com.hm.stock.domain.sms.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.Long;
import java.lang.String;

/**
* 短信记录 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class SiteSmsLog extends BaseEntity {

    @Schema(title = "手机号")
    @Parameter(description = "手机号")
    private String phone;

    @Schema(title = "验证码")
    @Parameter(description = "验证码")
    private String code;

    @Schema(title = "短信内容")
    @Parameter(description = "短信内容")
    private String context;

    @Schema(title = "短信接口响应Body")
    @Parameter(description = "短信接口响应Body")
    private String resBody;

    @Schema(title = "发送状态")
    @Parameter(description = "发送状态")
    private Long status;

}
