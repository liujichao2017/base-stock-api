package com.hm.stock.domain.site.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.lang.String;

/**
* 通用配置表 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataConfig extends BaseEntity{

    @Schema(title = "分组")
    @Parameter(description = "分组")
    @TableField("`group`")
    private String group;

    @Schema(title = "值类型: str(字符串),richText(富文本),image(图片),obj(JSON),arr(JSON)")
    @Parameter(description = "值类型: str(字符串),richText(富文本),image(图片),obj(JSON),arr(JSON)")
    private String type;

    @Schema(title = "键值")
    @Parameter(description = "键值")
    @TableField("`key`")
    private String key;

    @Schema(title = "对值")
    @Parameter(description = "对值")
    private String val;

    @Schema(title = "备注")
    @Parameter(description = "备注")
    private String remark;
}
