package com.example.album.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.album.common.enums.PrivacyTypeEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tb_album")
public class Album {

    @TableId(type = IdType.AUTO)
    private Integer albumId;

    private Integer userId;

    private String title;

    private String description;

    private PrivacyTypeEnum privacy;

    private Integer coverPhotoId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}