package com.example.album.dto;

import com.example.album.common.enums.PrivacyTypeEnum;
import lombok.Data;

@Data
public class PostUpdateDTO {
    private String caption;

    private PrivacyTypeEnum privacy;
}