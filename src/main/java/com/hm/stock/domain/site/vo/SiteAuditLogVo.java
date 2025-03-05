package com.hm.stock.domain.site.vo;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SiteAuditLogVo {
    @Schema(title = "ID")
    private Long id;

    @Schema(title = "类型: WEB: 后端用户. APP: 移动端用户")
    @Parameter(description = "类型: WEB: 后端用户. APP: 移动端用户")
    private String type;

    @Schema(title = "操作Id, 用户ID,或,会员ID")
    @Parameter(description = "操作Id, 用户ID,或,会员ID")
    private Long operateId;

    @Schema(title = "操作员")
    @Parameter(description = "操作员")
    private String operateName;

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

    /**
     * 创建时间 自动生成
     */
    @Schema(title = "创建时间")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @Schema(title = "修改时间")
    private LocalDateTime updateTime;

}
