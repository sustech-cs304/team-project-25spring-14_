package com.example.album.vo;

import com.example.album.common.enums.UserStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {

    private Integer userId;

    private String username;

    private String email;

    private String avatarUrl;

    private UserStatusEnum status;

    private Long storageUsed;

    private LocalDateTime createdAt;

    private LocalDateTime lastLogin;
}