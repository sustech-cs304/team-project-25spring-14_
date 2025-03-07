package com.example.album.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class PhotoUploadDTO {
    @NotNull(message = "相册ID不能为空")
    private Long albumId;

    private String title;

    private String location;

    private LocalDateTime capturedAt;

    private Boolean isFavorite;
}