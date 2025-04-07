package com.example.album.service;

import com.example.album.dto.PhotoStorageResult;
import com.example.album.entity.Photo;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface StorageService {
    /**
     * 存储照片文件并生成缩略图
     * @param file 上传的照片文件
     * @param userId 用户ID
     * @return 存储结果，包含文件URL和大小信息
     */
    PhotoStorageResult storePhoto(MultipartFile file, int userId) throws IOException;

    /**
     * 删除存储的照片
     * @param fileUrl 文件URL
     */
    void deletePhoto(String fileUrl) throws IOException;

    Photo getById(Integer coverPhotoId);

    List<Photo> getPhotosByAlbumId(Integer albumId);

    String getFullUrl(String fileUrl);
}