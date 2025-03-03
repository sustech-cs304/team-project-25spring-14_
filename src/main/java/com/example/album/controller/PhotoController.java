package com.example.album.controller;

import com.example.album.mapper.PhotoMapper;
import com.example.album.mapper.UserMapper;
import com.example.album.dto.PhotoStorageResult;
import com.example.album.entity.Photo;
import com.example.album.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/photos")
@Slf4j
public class PhotoController {

    @Autowired
    private StorageService storageService;

    @Autowired
    private PhotoMapper photoMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 上传照片
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadPhoto(
            @RequestParam("file") MultipartFile file,
            @RequestParam("albumId") Long albumId) {
        try {
            log.info("接收到照片上传请求，相册ID: {}, 文件名: {}", albumId, file.getOriginalFilename());

            // 获取当前用户ID (假设使用了Spring Security)
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long userId = Long.parseLong(auth.getName()); // 假设getName()返回用户ID

            // 存储照片
            PhotoStorageResult result = storageService.storePhoto(file, userId);

            // 保存照片记录到数据库
            Photo photo = new Photo();
            photo.setAlbumId(Math.toIntExact(albumId));
            photo.setUserId(Math.toIntExact(userId));
            photo.setFileName(result.getOriginalFilename());
            photo.setFileUrl(result.getFileUrl());
            photo.setThumbnailUrl(result.getThumbnailUrl());
//            photo.setFileSize(result.getFileSize());
            photo.setCapturedAt(result.getCapturedAt());
            photo.setCreatedAt(LocalDateTime.now());

            // 使用MyBatis Mapper插入数据
            photoMapper.insert(photo);
            log.info("照片信息已保存到数据库，ID: {}", photo.getPhotoId());

            // 更新用户存储空间使用量
//            userMapper.updateStorageUsed(userId, result.getFileSize());

            // 返回结果
            Map<String, Object> response = new HashMap<>();
            response.put("photo", photo);
            response.put("message", "照片上传成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("照片上传失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("照片上传失败: " + e.getMessage());
        }
    }

    /**
     * 删除照片
     */
    @DeleteMapping("/{photoId}")
    public ResponseEntity<?> deletePhoto(@PathVariable Long photoId) {
        try {
            log.info("接收到照片删除请求，照片ID: {}", photoId);

            // 获取当前用户ID
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long userId = Long.parseLong(auth.getName());

            // 查找照片
            Photo photo = photoMapper.selectById(photoId);
            if (photo == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("照片未找到");
            }

            // 验证所有权
            if (!Long.valueOf(photo.getUserId()).equals(userId)) {
                log.warn("用户 {} 尝试删除不属于他的照片 {}", userId, photoId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权删除此照片");
            }

            // 删除物理文件
            storageService.deletePhoto(photo.getFileUrl());

            // 更新用户存储空间使用量
//            userMapper.updateStorageUsed(userId, -photo.getFileSize());

            // 删除数据库记录
            photoMapper.delete(photoId);
            log.info("照片ID: {} 已成功删除", photoId);

            return ResponseEntity.ok("照片删除成功");
        } catch (Exception e) {
            log.error("照片删除失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("照片删除失败: " + e.getMessage());
        }
    }
}