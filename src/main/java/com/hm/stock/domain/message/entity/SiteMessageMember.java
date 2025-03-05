package com.hm.stock.domain.message.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.Long;

/**
* 用户通知 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class SiteMessageMember extends BaseEntity {

    @Schema(title = "会员Id")
    @Parameter(description = "会员Id")
    private Long memberId;

    @Schema(title = "消息ID")
    @Parameter(description = "消息ID")
    private Long messageId;

}
