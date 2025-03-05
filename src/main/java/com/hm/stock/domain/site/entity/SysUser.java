package com.hm.stock.domain.site.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.hm.stock.modules.common.BaseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.Long;
import java.util.Date;
import java.lang.String;

/**
* 用户信息表 实体类
*/
@EqualsAndHashCode()
@Data
public class SysUser  {

    @Schema(title = "用户ID")
    @Parameter(description = "用户ID")
    @TableId(type = IdType.AUTO)
    private Long userId;


    @Schema(title = "用户类型（00系统用户）")
    @Parameter(description = "用户类型（00系统用户）")
    private String userType;

    @Schema(title = "上级ID")
    @Parameter(description = "上级ID")
    private Long parentId;

    @Schema(title = "邀请码")
    @Parameter(description = "邀请码")
    private String inviteCode;

    @Schema(title = "客服地址")
    @Parameter(description = "客服地址")
    private String onlineService;

    @Schema(title = "入金客户地址")
    @Parameter(description = "入金客户地址")
    private String fundService;

}
