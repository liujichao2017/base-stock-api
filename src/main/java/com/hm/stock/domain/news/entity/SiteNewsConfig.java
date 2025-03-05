package com.hm.stock.domain.news.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.Long;
import java.lang.String;

/**
* 新闻配置 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class SiteNewsConfig extends BaseEntity {

    @Schema(title = "国家")
    @Parameter(description = "国家)")
    private String country;

    @Schema(title = "新闻源: JS(匠山), MY_v1(自爬 v1是版本,接口格式相同则不需要更换)")
    @Parameter(description = "新闻源: JS(匠山), MY_v1(自爬 v1是版本,接口格式相同则不需要更换)")
    private String source;

    @Schema(title = "参数配置")
    @Parameter(description = "参数配置")
    private String json;

    @Schema(title = "状态：1、启用，0、停用")
    @Parameter(description = "状态：1、启用，0、停用")
    private String status;

}
