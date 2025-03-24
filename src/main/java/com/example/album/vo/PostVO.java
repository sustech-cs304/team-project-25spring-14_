package com.example.album.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostVO {
    private Integer postId;

    private Integer userId;

    private String username;

    private String userAvatar;

    private Integer photoId;

    private String photoUrl;

    private String thumbnailUrl;

    private String caption;

    private String privacy;

    private Integer likeCount;

    private Integer commentCount;

    private Boolean isLiked;  // 当前登录用户是否已点赞

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}