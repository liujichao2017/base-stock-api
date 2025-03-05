package com.hm.stock.domain.message.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
* 用户消息 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class MemberMessage extends BaseEntity {

    @Schema(title = "会员Id")
    @Parameter(description = "会员Id")
    private Long memberId;

    @Schema(title = "消息类型: 1.弹窗, 2.消息列表")
    @Parameter(description = "消息类型: 1.弹窗, 2.消息列表")
    private String type;

    @Schema(title = "消息源: 1.系统, 2.新股")
    @Parameter(description = "消息源: 1.系统, 2.新股")
    private BigDecimal source;

    @Schema(title = "标题")
    @Parameter(description = "标题")
    private String title;

    @Schema(title = "消息内容")
    @Parameter(description = "消息内容")
    private String content;

    @Schema(title = "阅读状态：1 未阅读，2 已阅读")
    @Parameter(description = "阅读状态：1 未阅读，2 已阅读")
    private Long readStatus;

    @Schema(title = "温馨提示")
    @Parameter(description = "温馨提示")
    private String kindTips;

    @Schema(title = "源Id")
    @Parameter(description = "源Id")
    private Long productId;

    @Schema(title = "ipo市场id")
    @Parameter(description = "ipo市场id")
    @TableField(exist = false)
    private Long marketId;
}
