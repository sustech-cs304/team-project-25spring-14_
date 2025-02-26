package com.example.album.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tb_admin_log")
public class AdminLog {

    @TableId(type = IdType.AUTO)
    private Integer logId;

    private Integer adminId;

    private String action;

    private String targetType;

    private Integer targetId;

    private LocalDateTime createdAt;
}