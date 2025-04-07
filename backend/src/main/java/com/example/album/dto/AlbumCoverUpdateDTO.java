package com.example.album.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
public class AlbumCoverUpdateDTO {
    @NotNull(message = "照片文件不能为空")
    private MultipartFile photo;
}