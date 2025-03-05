package com.hm.stock.domain.member.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.Long;
import java.lang.String;

/**
* 用户银行卡 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class MemberBank extends BaseEntity {

    @Schema(title = "会员ID")
    @Parameter(description = "会员ID")
    private Long memberId;

    @Schema(title = "预留字段,根据项目需要使用: 银行名称")
    @Parameter(description = "预留字段,根据项目需要使用: 银行名称")
    private String bank1;

    @Schema(title = "预留字段,根据项目需要使用: 银行卡号")
    @Parameter(description = "预留字段,根据项目需要使用: 银行卡号")
    private String bank2;

    @Schema(title = "预留字段,根据项目需要使用: 银行代码")
    @Parameter(description = "预留字段,根据项目需要使用: 银行代码")
    private String bank3;

    @Schema(title = "预留字段,根据项目需要使用: 开户人名称")
    @Parameter(description = "预留字段,根据项目需要使用: 开户人名称")
    private String bank4;

    @Schema(title = "预留字段,根据项目需要使用")
    @Parameter(description = "预留字段,根据项目需要使用")
    private String bank5;

    @Schema(title = "预留字段,根据项目需要使用")
    @Parameter(description = "预留字段,根据项目需要使用")
    private String bank6;

    @Schema(title = "预留字段,根据项目需要使用")
    @Parameter(description = "预留字段,根据项目需要使用")
    private String bank7;

    @Schema(title = "预留字段,根据项目需要使用")
    @Parameter(description = "预留字段,根据项目需要使用")
    private String bank8;

}
