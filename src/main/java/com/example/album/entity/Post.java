package com.example.album.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.album.common.enums.PrivacyTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@TableName("tb_post")
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @TableId(type = IdType.AUTO)
    private Integer postId;

    private Integer userId;

    private Integer likeCount = 0;

    private String caption;

    private PrivacyTypeEnum privacy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}