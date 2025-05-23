package com.example.album.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostVO {
    private Integer postId;

    private Integer userId;

    private String username;

    private String userAvatar;

    private List<PhotoVO> photos;

    private String caption;

    private String privacy;

    private Integer likeCount;

    private Integer commentCount;

    private Boolean isLiked;  // 当前登录用户是否已点赞

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}