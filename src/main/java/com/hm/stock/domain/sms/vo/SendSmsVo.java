package com.hm.stock.domain.sms.vo;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SendSmsVo {
    @Schema(title = "短信配置列表ID")
    @Parameter(description = "短信配置列表ID")
    @NotBlank
    private Long id;

    @Schema(title = "手机号: 需要拼接区号")
    @Parameter(description = "手机号: 需要拼接区号")
    @NotBlank
    private String phone;

    @Schema(title = "模板类型: 1: 注册, 2: 找回密码")
    @Parameter(description = "模板类型: 1: 注册, 2: 找回密码")
    @NotBlank
    private String type;


    @Schema(title = "判断手机号是否存在, type = 1, 需要校验 传 1, 其他传0 (暂定)")
    @Parameter(description = "判断手机号是否存在, type = 1, 需要校验 传 1, 其他传0 (暂定)")
    private String checkPhone = "0";

    @Schema(title = "模拟发送: 1:是 .0:否")
    @Parameter(description = "模拟发送: 1:是 .0:否")
    private String imitation = "0";


}
