package com.example.album.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PostCreateDTO {
    @NotNull
    private Integer photoId;

    private String caption;

    private String privacy = "public";  // 默认为公开
}