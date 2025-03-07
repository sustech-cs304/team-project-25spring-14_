package com.example.album.service.impl;

import com.example.album.dto.PhotoStorageResult;
import com.example.album.entity.Photo;
import com.example.album.mapper.PhotoMapper;
import com.example.album.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final PhotoMapper photoMapper;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.domain}")
    private String domain;

    @Value("${app.storage.location:${app.upload.dir}/storage}")
    private String storageLocation;

    @Value("${app.thumbnail.location:${app.upload.dir}/thumbnails}")
    private String thumbnailLocation;

    @Override
    public PhotoStorageResult storePhoto(MultipartFile file, Long userId) throws IOException {
        log.info("开始存储照片，用户ID: {}, 文件名: {}", userId, file.getOriginalFilename());

        // 创建必要的目录
        createDirectories();

        // 生成唯一文件名
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = originalFilename.contains(".") ?
                originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        // 创建用户目录
        String userDir = storageLocation + "/" + userId;
        File userDirectory = new File(userDir);
        if (!userDirectory.exists()) {
            userDirectory.mkdirs();
        }

        // 存储原始文件
        Path originalFilePath = Paths.get(userDir, uniqueFilename);
        Files.copy(file.getInputStream(), originalFilePath, StandardCopyOption.REPLACE_EXISTING);

        // 获取图像尺寸
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // 生成缩略图
        String thumbFilename = "thumb_" + uniqueFilename;
        String thumbnailDir = thumbnailLocation + "/" + userId;
        new File(thumbnailDir).mkdirs();
        Path thumbFilePath = Paths.get(thumbnailDir, thumbFilename);

        Thumbnails.of(originalFilePath.toFile())
                .size(200, 200)
                .toFile(thumbFilePath.toFile());

        // 生成访问URL
        String fileUrl = domain + "/storage/" + userId + "/" + uniqueFilename;
        String thumbUrl = domain + "/thumbnails/" + userId + "/" + thumbFilename;

        // 创建结果对象
        PhotoStorageResult result = new PhotoStorageResult();
        result.setOriginalFilename(originalFilename);
        result.setFileUrl(fileUrl);
        result.setThumbnailUrl(thumbUrl);
//        result.setFileSize(Files.size(originalFilePath));
        result.setWidth(width);
        result.setHeight(height);
        result.setCapturedAt(LocalDateTime.now()); // 在实际实现中，应从EXIF数据中提取

        log.info("照片存储成功: {}", fileUrl);
        return result;
    }

    @Override
    public void deletePhoto(String fileUrl) throws IOException {
        log.info("删除照片: {}", fileUrl);

        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        // 从URL中提取路径
        String relativePath = fileUrl.replace(domain + "/storage/", "");
        Path filePath = Paths.get(storageLocation, relativePath);

        // 尝试删除原始文件
        if (Files.exists(filePath)) {
            Files.delete(filePath);
            log.info("原始文件已删除: {}", filePath);
        } else {
            log.warn("文件不存在: {}", filePath);
        }

        // 尝试删除缩略图
        String userId = relativePath.substring(0, relativePath.indexOf('/'));
        String filename = relativePath.substring(relativePath.lastIndexOf('/') + 1);
        String thumbFilename = "thumb_" + filename;
        Path thumbFilePath = Paths.get(thumbnailLocation, userId, thumbFilename);

        if (Files.exists(thumbFilePath)) {
            Files.delete(thumbFilePath);
            log.info("缩略图已删除: {}", thumbFilePath);
        } else {
            log.warn("缩略图不存在: {}", thumbFilePath);
        }
    }

    @Override
    public Photo getById(Integer photoId) {
        if (photoId == null) {
            return null;
        }
        return photoMapper.selectById(Long.valueOf(photoId));
    }

    @Override
    public List<Photo> getPhotosByAlbumId(Integer albumId) {
        if (albumId == null) {
            throw new IllegalArgumentException("Album ID cannot be null");
        }
        return photoMapper.selectByAlbumId(albumId);
    }

    private void createDirectories() {
        new File(storageLocation).mkdirs();
        new File(thumbnailLocation).mkdirs();
    }
    /**
     * 将相对文件路径转换为完整的URL
     * @param relativePath 相对路径，例如 "/uploads/photos/abc.jpg"
     * @return 完整URL，例如 "http://localhost:8080/uploads/photos/abc.jpg"
     */
    public String getFullUrl(String relativePath) {
        if (relativePath == null) {
            return null;
        }

        // 如果已经是完整URL，直接返回
        if (relativePath.startsWith("http://") || relativePath.startsWith("https://")) {
            return relativePath;
        }

        // 确保路径正确连接
        if (domain.endsWith("/") && relativePath.startsWith("/")) {
            return domain + relativePath.substring(1);
        } else if (!domain.endsWith("/") && !relativePath.startsWith("/")) {
            return domain + "/" + relativePath;
        } else {
            return domain + relativePath;
        }
    }
}