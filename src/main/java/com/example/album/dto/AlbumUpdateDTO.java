package com.example.album.dto;

import com.example.album.common.enums.PrivacyTypeEnum;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class AlbumUpdateDTO {

    @Size(max = 100, message = "相册标题最多100个字符")
    private String title;

    private String description;

    private PrivacyTypeEnum privacy;

    private Integer coverPhotoId;
}