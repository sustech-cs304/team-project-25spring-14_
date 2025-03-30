package com.example.album.vo;

import com.example.album.common.enums.PrivacyTypeEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlbumVO {

    private Integer albumId;

    private Integer userId;

    private Integer photoCount;

    private String title;

    private String description;

    private PrivacyTypeEnum privacy;

    private Integer coverPhotoId;
}