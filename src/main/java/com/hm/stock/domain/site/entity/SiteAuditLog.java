package com.hm.stock.domain.site.entity;

import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* 审计日志 实体类
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class SiteAuditLog extends BaseEntity {

    @Schema(title = "操作Id, 用户ID,或,会员ID")
    @Parameter(description = "操作Id, 用户ID,或,会员ID")
    private Long memberId;

    @Schema(title = "操作接口名称")
    @Parameter(description = "操作接口名称")
    private String urlName;

    @Schema(title = "操作接口名称")
    @Parameter(description = "操作接口名称")
    private String urlMethod;

    @Schema(title = "接口路径")
    @Parameter(description = "接口路径")
    private String urlPath;

    @Schema(title = "请求参数")
    @Parameter(description = "请求参数")
    private String reqBody;

    @Schema(title = "响应参数")
    @Parameter(description = "响应参数")
    private String resBody;

    @Schema(title = "ip地址")
    @Parameter(description = "ip地址")
    private String addr;

    @Schema(title = "结果: 1:成功, 0:失败")
    @Parameter(description = "结果: 1:成功, 0:失败")
    private String status;

    @Schema(title = "耗时, 毫秒")
    @Parameter(description = "耗时, 毫秒")
    private Long time;


}
