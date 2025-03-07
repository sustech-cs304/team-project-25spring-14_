package com.example.album.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PhotoVO {
    private Integer photoId;

    private Integer albumId;

    private Integer userId;

    private String fileName;

    private String fileUrl;

    private String thumbnailUrl;

    private Long fileSize;

    private String description;

    private String title;

    private String location;

    private LocalDateTime capturedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
//    private List<TagVO> tags;
}