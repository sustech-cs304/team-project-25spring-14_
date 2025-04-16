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
import java.net.URI;
import java.net.URISyntaxException;
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
    /**
     * AI-generated-content
     * tool: claude
     * version: latest
     * usage: dont know how to user the dir in properties and ask
     * copy
     */
    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.domain}")
    private String domain;

    @Value("${app.storage.location:${app.upload.dir}/storage}")
    private String storageLocation;

    @Value("${app.thumbnail.location:${app.upload.dir}/thumbnails}")
    private String thumbnailLocation;

    /**
     * AI-generated-content
     * tool: claude
     * version: latest
     * usage: ask how to deal with multipart file storage
     * copy it and its dto
     */
    @Override
    public PhotoStorageResult storePhoto(MultipartFile file, int userId) throws IOException {
        log.info("开始存储文件，用户ID: {}, 文件名: {}", userId, file.getOriginalFilename());
        createDirectories();

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
        log.info(originalFilePath.toString());
        Files.copy(file.getInputStream(), originalFilePath, StandardCopyOption.REPLACE_EXISTING);

        // 检测是否为视频文件
        boolean isVideo = isVideoFile(file);
        log.info("文件类型: {}", isVideo ? "视频" : "图片");

        // 创建结果对象
        PhotoStorageResult result = new PhotoStorageResult();
        result.setOriginalFilename(originalFilename);

        // 生成访问URL
        String fileUrl = domain + "/uploads/storage/" + userId + "/" + uniqueFilename;
        result.setFileUrl(fileUrl);

        if (!isVideo) {
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

            String thumbUrl = domain + "/uploads/thumbnails/" + userId + "/" + thumbFilename;
            result.setThumbnailUrl(thumbUrl);
            result.setWidth(width);
            result.setHeight(height);
        } else {
            result.setThumbnailUrl(null);
        }

        result.setCapturedAt(LocalDateTime.now());
        log.info("文件存储成功: {}", fileUrl);
        return result;
    }

    private boolean isVideoFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null && contentType.startsWith("video/")) {
            return true;
        }

        String filename = file.getOriginalFilename();
        if (filename != null) {
            String extension = filename.toLowerCase();
            return extension.endsWith(".mp4") || extension.endsWith(".avi") ||
                    extension.endsWith(".mov") || extension.endsWith(".wmv") ||
                    extension.endsWith(".flv") || extension.endsWith(".mkv") ||
                    extension.endsWith(".webm");
        }

        return false;
    }


    /**
     * AI-generated-content
     * tool: claude
     * version: latest
     * usage: ask how to deal with multipart file delete
     * copy
     */
    @Override
    public void deletePhoto(String fileUrl) throws IOException, URISyntaxException {
        log.info("删除照片: {}", fileUrl);

        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        // 1. 从URL中提取相对路径（移除协议和域名部分）
        URI uri = new URI(fileUrl);
        String relativePath = uri.getPath(); // 例如: "/uploads/storage/1/xxx.png"

        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }
        Path filePath = Paths.get(storageLocation, relativePath).normalize();

        if (!filePath.startsWith(Paths.get(storageLocation).normalize())) {
            throw new SecurityException("非法路径访问: " + filePath);
        }

        if (Files.exists(filePath)) {
            Files.delete(filePath);
            log.info("原始文件已删除: {}", filePath);
        } else {
            log.warn("文件不存在: {}", filePath);
        }

        String userId = relativePath.substring(relativePath.indexOf("storage/") + 8, relativePath.indexOf('/', relativePath.indexOf("storage/") + 8));
        String filename = relativePath.substring(relativePath.lastIndexOf('/') + 1);
        String thumbFilename = "thumb_" + filename;
        Path thumbFilePath = Paths.get(thumbnailLocation, userId, thumbFilename).normalize();

        if (Files.exists(thumbFilePath)) {
            Files.delete(thumbFilePath);
            log.info("缩略图已删除: {}", thumbFilePath);
        } else {
            log.warn("缩略图不存在: {}", thumbFilePath);
        }
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