package com.example.album.service.impl;

import com.example.album.dto.PhotoStorageResult;
import com.example.album.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class StorageServiceImpl implements StorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.domain}")
    private String domain;

    @Override
    public PhotoStorageResult storePhoto(MultipartFile file, Long userId) throws IOException {
        log.info("开始存储照片，用户ID: {}, 文件名: {}", userId, file.getOriginalFilename());

        // 创建用户目录
        String userDirPath = uploadDir + "/" + userId;
        Path userDir = Paths.get(userDirPath);
        if (!Files.exists(userDir)) {
            Files.createDirectories(userDir);
        }

        // 生成唯一文件名
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        // 存储原始文件
        Path originalFilePath = userDir.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), originalFilePath, StandardCopyOption.REPLACE_EXISTING);

        // 获取图像尺寸
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // 生成缩略图
        String thumbFilename = "thumb_" + uniqueFilename;
        Path thumbFilePath = userDir.resolve(thumbFilename);

        Thumbnails.of(originalFilePath.toFile())
                .size(200, 200)
                .toFile(thumbFilePath.toFile());

        // 生成访问URL
        String fileUrl = domain + "/uploads/" + userId + "/" + uniqueFilename;
        String thumbUrl = domain + "/uploads/" + userId + "/" + thumbFilename;

        // 创建结果对象
        PhotoStorageResult result = new PhotoStorageResult(
                originalFilename,
                fileUrl,
                thumbUrl//,
//                Files.size(originalFilePath),
//                Files.size(thumbFilePath)
        );

        // 设置图像尺寸
        result.setWidth(width);
        result.setHeight(height);

        log.info("照片存储成功: {}", fileUrl);
        return result;
    }

    @Override
    public void deletePhoto(String fileUrl) throws IOException {
        log.info("删除照片: {}", fileUrl);

        // 提取相对路径
        String relativePath = fileUrl.replace(domain + "/uploads/", "");
        Path filePath = Paths.get(uploadDir, relativePath);

        if (Files.exists(filePath)) {
            Files.delete(filePath);
            log.info("原始文件已删除: {}", filePath);
        } else {
            log.warn("文件不存在: {}", filePath);
        }

        // 尝试删除缩略图
        String thumbPath = relativePath.replace(
                relativePath.substring(relativePath.lastIndexOf("/")+1),
                "thumb_" + relativePath.substring(relativePath.lastIndexOf("/")+1)
        );

        Path thumbFilePath = Paths.get(uploadDir, thumbPath);
        if (Files.exists(thumbFilePath)) {
            Files.delete(thumbFilePath);
            log.info("缩略图已删除: {}", thumbFilePath);
        }
    }
}