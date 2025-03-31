package com.example.album.dto;

import com.example.album.common.enums.PrivacyTypeEnum;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PostCreateWithPhotoDTO {
    @NotNull(message = "照片文件不能为空")
    private List<MultipartFile> photo;

    private String caption;

    private PrivacyTypeEnum privacy;
}