package com.example.album.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PhotoUpdateDTO {

    private Integer albumId;

    private Boolean isFavorite;

    private String tag;
}