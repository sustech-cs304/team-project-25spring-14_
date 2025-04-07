package com.example.album.dto;

import com.example.album.common.enums.PrivacyTypeEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PostCreateDTO {
    @NotNull
    private Integer photoId;

    private String caption;

    private PrivacyTypeEnum privacy;
}