package com.hm.stock.domain.message.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.Long;
import java.lang.String;

/**
* 公告 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class SiteMessage extends BaseEntity {
    @Schema(title = "值类型: richText(富文本),image(图片)")
    @Parameter(description = "值类型: richText(富文本),image(图片)")
    private String type;

    @Schema(title = "标题")
    @Parameter(description = "标题")
    private String title;

    @Schema(title = "消息内容")
    @Parameter(description = "消息内容")
    private String content;

    @Schema(title = "状态：1 通知，2 不通知")
    @Parameter(description = "状态：1 通知，2 不通知")
    private Long status;

    @Schema(title = "已读状态: 1已读,0: 未读")
    @Parameter(description = "已读状态: 1已读,0: 未读")
    @TableField(exist = false)
    private String readStatus;

}
