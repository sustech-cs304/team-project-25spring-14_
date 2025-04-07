package com.example.album.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tb_photo_ai")
public class PhotoAi {

    @TableId
    private Integer photoId;

    private String[] objects;

    private String[] people;

    private String scene;

    private LocalDateTime processedAt;
}