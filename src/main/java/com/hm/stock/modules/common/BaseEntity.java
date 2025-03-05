package com.hm.stock.modules.common;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class BaseEntity implements Serializable {
    @TableId(type = IdType.AUTO)
    @Schema(title = "ID")
    private Long id;

    /**
     * 创建时间 自动生成
     */
    @Schema(title = "创建时间")
    private Date createTime;

    /**
     * 修改时间
     */
    @Schema(title = "修改时间")
    private Date updateTime;
}
