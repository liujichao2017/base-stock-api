package com.hm.stock.domain.news.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.Long;
import java.util.Date;
import java.lang.String;

/**
* 新闻资讯 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class SiteNews extends BaseEntity {

    @Schema(title = "新闻类型：1、财经要闻，2、经济数据，3、全球股市，4、7*24全球，5、商品资讯，6、上市公司，7、全球央行")
    @Parameter(description = "新闻类型：1、财经要闻，2、经济数据，3、全球股市，4、7*24全球，5、商品资讯，6、上市公司，7、全球央行")
    private Long type;

    @Schema(title = "新闻标题")
    @Parameter(description = "新闻标题")
    private String title;

    @Schema(title = "来源id")
    @Parameter(description = "来源id")
    private String sourceId;

    @Schema(title = "新闻源: JS(匠山), MY_v1(自爬 v1是版本,接口格式相同则不需要更换)")
    @Parameter(description = "新闻源: JS(匠山), MY_v1(自爬 v1是版本,接口格式相同则不需要更换)")
    private String source;

    @Schema(title = "浏览量")
    @Parameter(description = "浏览量")
    private Long views;

    @Schema(title = "显示时间")
    @Parameter(description = "显示时间")
    private Date showTime;

    @Schema(title = "图片地址")
    @Parameter(description = "图片地址")
    private String imgurl;

    @Schema(title = "新闻内容")
    @Parameter(description = "新闻内容")
    private String content;

    @Schema(title = "状态：1、启用，0、停用")
    @Parameter(description = "状态：1、启用，0、停用")
    private String status;

}
