
package com.example.album.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhotoStorageResult {
    private String originalFilename;
    private String fileUrl;
    private String thumbnailUrl;
//    private long fileSize;
//    private long thumbnailSize;
    private LocalDateTime capturedAt;
    private Integer width;
    private Integer height;

    // 用于基本属性的构造函数
    public PhotoStorageResult(String originalFilename, String fileUrl,
                              String thumbnailUrl){//}, long fileSize, long thumbnailSize) {
        this.originalFilename = originalFilename;
        this.fileUrl = fileUrl;
        this.thumbnailUrl = thumbnailUrl;
//        this.fileSize = fileSize;
//        this.thumbnailSize = thumbnailSize;
    }
}