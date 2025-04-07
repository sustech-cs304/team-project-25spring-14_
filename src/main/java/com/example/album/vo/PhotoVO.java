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

    private String tag;

    private String location;

    private LocalDateTime capturedAt;

    private LocalDateTime createdAt;
//    private List<TagVO> tags;
}