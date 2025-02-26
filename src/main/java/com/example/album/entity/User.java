package com.example.album.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.album.common.enums.UserStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tb_user")
public class User {

    @TableId(type = IdType.AUTO)
    private Integer userId;

    private String username;

    private String password;

    private String email;

    private String avatarUrl;

    private UserStatusEnum status;

    private Long storageUsed;

    private LocalDateTime createdAt;

    private LocalDateTime lastLogin;
}