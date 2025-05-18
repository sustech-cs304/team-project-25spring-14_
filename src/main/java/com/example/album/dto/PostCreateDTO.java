package com.example.album.dto;

import com.example.album.common.enums.PrivacyTypeEnum;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PostCreateDTO {
    @NotEmpty(message = "至少需要一个照片ID")
    private List<Integer> photoIds;

    private String caption;

    private PrivacyTypeEnum privacy;
}