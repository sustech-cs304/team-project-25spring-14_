package com.example.album.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.album.common.enums.ResourceTypeEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tb_report")
public class Report {

    @TableId(type = IdType.AUTO)
    private Integer reportId;

    private Integer reporterId;

    private ResourceTypeEnum resourceType;

    private Integer resourceId;

    private String reason;

    private String status;

    private Integer reviewedBy;

    private LocalDateTime createdAt;
}