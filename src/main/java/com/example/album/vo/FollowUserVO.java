package com.example.album.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FollowUserVO {
    private Integer userId;
    private String username;
    private String avatarUrl;
    private LocalDateTime followTime;
    private Boolean isFollowing;  // 当前用户是否已关注此用户
}