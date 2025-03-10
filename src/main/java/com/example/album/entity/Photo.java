package com.example.album.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@TableName("tb_photo")
@NoArgsConstructor
@AllArgsConstructor
public class Photo {

    @TableId(type = IdType.AUTO)
    private Integer photoId;

    private Integer albumId;

    private Integer userId;

    private String tagName;

    private String fileName;

    private String fileUrl;

    private String thumbnailUrl;

    private Boolean isFavorite;

    private LocalDateTime capturedAt;

    private LocalDateTime createdAt;

}