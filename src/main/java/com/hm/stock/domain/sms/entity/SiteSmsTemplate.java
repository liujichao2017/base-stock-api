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
public class SiteSmsTemplate extends BaseEntity {

    @Schema(title = "模板类型: 1: 注册, 2: 找回密码")
    @Parameter(description = "模板类型: 1: 注册, 2: 找回密码")
    private String type;

    @Schema(title = "短信内容模板, {code} 验证码占位符")
    @Parameter(description = "短信内容模板, {code} 验证码占位符")
    private String context;

}
