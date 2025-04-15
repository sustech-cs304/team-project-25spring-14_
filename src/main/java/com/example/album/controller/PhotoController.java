package com.example.album.controller;

import com.example.album.dto.PhotoUploadDTO;
import com.example.album.dto.PhotoUpdateDTO;
import com.example.album.dto.PhotoStorageResult;
import com.example.album.entity.Photo;
import com.example.album.entity.Result;
import com.example.album.mapper.PhotoMapper;
import com.example.album.service.AlbumService;
import com.example.album.service.StorageService;
import com.example.album.utils.ThreadLocalUtil;
import com.example.album.vo.PhotoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import com.example.album.service.ImageService;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/photos")
@Slf4j
@RequiredArgsConstructor
public class PhotoController {

    private final AlbumService albumService;
    private final StorageService storageService;
    private final PhotoMapper photoMapper;
    private final ImageService imageService;


    /**
     * AI-generated-content
     * tool: claude
     * version: latest
     * usage: give it the key of photo to ask it to generate the Photo construct
     * copy
     */
    @PostMapping("/upload")
    public Result<Map<String, Object>> uploadPhoto(
            @RequestParam("file") MultipartFile file,
            @Valid @ModelAttribute PhotoUploadDTO uploadDTO) {
        try {
            log.info("接收到照片上传请求，相册ID: {}, 文件名: {}", uploadDTO.getAlbumId(), file.getOriginalFilename());

            Map<String, Object> claims = ThreadLocalUtil.get();
            int userId = 0;
            if (claims != null) {
                userId = ((Number) claims.get("id")).intValue();
                log.info("从ThreadLocal获取的用户ID: {}", userId);
            } else return null;
            // 存储照片
            PhotoStorageResult result = storageService.storePhoto(file, userId);
//            String tag = imageService.ai_classify(result.getFileUrl());  //直接用这个url发送给python后端，会返回一个字符串
            // 保存照片记录到数据库
            Photo photo = new Photo();
            photo.setAlbumId(Math.toIntExact(uploadDTO.getAlbumId()));
            photo.setUserId(Math.toIntExact(userId));
            photo.setFileName(result.getOriginalFilename());
            photo.setFileUrl(result.getFileUrl());
            photo.setThumbnailUrl(result.getThumbnailUrl());
            photo.setLocation(uploadDTO.getLocation());
//            photo.setFileSize(result.getFileSize());
//            photo.setTagName(tag);  // 首先默认使用我进行分类的标签，如果需要修改，就再次调用这个方法
            photo.setCapturedAt(result.getCapturedAt());
            photo.setCreatedAt(LocalDateTime.now());
//            photo.setPostId(uploadDTO.getPostId());
            if (uploadDTO.getIsFavorite() != null) {
                photo.setIsFavorite(uploadDTO.getIsFavorite());
            }

            photoMapper.insert(photo);
            log.info("照片信息已保存到数据库，ID: {}", photo.getPhotoId());
            PhotoVO photoVO = convertToPhotoVO(photo);
            Map<String, Object> response = new HashMap<>();
            response.put("photo", photoVO);
            response.put("message", "照片上传成功");

            return Result.success(response);
        } catch (Exception e) {
            log.error("照片上传失败", e);
            return Result.error("照片上传失败");
        }
    }

    @GetMapping("/{photoId}")
    public Result<Map<String, Object>> getPhotoDetail(@PathVariable int photoId) {
        try {
            Photo photo = photoMapper.selectById(photoId);
            if (photo == null) {
                throw new Exception("照片未找到");
            }
            PhotoVO photoVO = convertToPhotoVO(photo);

            Map<String, Object> response = new HashMap<>();
            response.put("photo", photoVO);

            return Result.success(response);
        } catch (Exception e) {
            log.error("获取照片详情失败", e);
            return Result.error("获取照片详情失败");
        }
    }


    /**
     * AI-generated-content
     * tool: claude
     * version: latest
     * usage: ask how to do it in java's stream that in high efficiency
     * copy directly
     */
    @GetMapping("/album/{albumId}")
    public Result<Map<String, Object>> getPhotosByAlbum(@PathVariable int albumId) {
        try {
            List<Photo> photos = photoMapper.selectByAlbumId(Math.toIntExact(albumId));

            List<PhotoVO> photoVOList = photos.stream()
                    .map(this::convertToPhotoVO)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("photos", photoVOList);
            response.put("count", photoVOList.size());

            return Result.success(response);
        } catch (Exception e) {
            log.error("获取相册照片失败", e);
            return Result.error("获取相册照片失败");
        }
    }

    /**
     * AI-generated-content
     * tool: claude
     * version: latest
     * usage: just give the template of update album, and it can generate
     * copy and add userId check
     */
    @PutMapping("/{photoId}")
    public Result<Map<String, Object>> updatePhoto(
            @PathVariable int photoId,
            @Valid @ModelAttribute PhotoUpdateDTO updateDTO) {
        try {
            Map<String, Object> claims = ThreadLocalUtil.get();
            int userId = 0;
            if (claims != null) {
                userId = ((Number) claims.get("id")).intValue();
                log.info("从ThreadLocal获取的用户ID: {}", userId);
            } else return null;
            Photo photo = photoMapper.selectById(photoId);
            if (photo == null) {
                throw new Exception("照片未找到");
            }

            if (!photo.getUserId().equals(userId)) {
                log.warn("用户 {} 尝试更新不属于他的照片 {}", userId, photoId);
                throw new Exception("无权更新此照片");
            }

            boolean hasUpdates = false;

            if (updateDTO.getIsFavorite() != null && updateDTO.getTag() != null) {
                photo.setIsFavorite(updateDTO.getIsFavorite());
                photo.setTagName(updateDTO.getTag());
                hasUpdates = true;
            }

            if (updateDTO.getAlbumId() != null && !updateDTO.getAlbumId().equals(photo.getAlbumId())) {
                albumService.checkAlbumAccess(updateDTO.getAlbumId(), Math.toIntExact(userId));
                photo.setAlbumId(Math.toIntExact(updateDTO.getAlbumId()));
                hasUpdates = true;
            }

            if (hasUpdates) {
                photo.setCreatedAt(LocalDateTime.now());
                photoMapper.updateById(photo);
                log.info("照片信息已更新，ID: {}", photoId);
            }
            PhotoVO photoVO = convertToPhotoVO(photo);

            Map<String, Object> response = new HashMap<>();
            response.put("photo", photoVO);
            response.put("message", "照片信息更新成功");

            return Result.success(response);
        } catch (Exception e) {
            log.error("更新照片信息失败", e);
            return Result.error("更新照片信息失败");
        }
    }

    /**
     * AI-generated-content
     * tool: claude
     * version: latest
     * usage: just give the template of delete album, and it can generate
     * copy and add userId check
     */
    @DeleteMapping("/{photoId}")
    public Result<Map<String, Object>> deletePhoto(@PathVariable int photoId) {
        try {
            log.info("接收到照片删除请求，照片ID: {}", photoId);
            Map<String, Object> claims = ThreadLocalUtil.get();
            int userId = 0;
            if (claims != null) {
                userId = ((Number) claims.get("id")).intValue();
                log.info("从ThreadLocal获取的用户ID: {}", userId);
            } else return null;
            Photo photo = photoMapper.selectById(photoId);
            if (photo == null) {
                throw new Exception("照片未找到");
            }
            if (!photo.getUserId().equals(userId)) {
                log.warn("用户 {} 尝试删除不属于他的照片 {}", userId, photoId);
                throw new Exception("无权删除此照片");
            }
            storageService.deletePhoto(photo.getFileUrl());
            photoMapper.deleteById(photoId);
            log.info("照片ID: {} 已成功删除", photoId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "照片删除成功");

            return Result.success(response);
        } catch (Exception e) {
            log.error("照片删除失败", e);
            return Result.error("照片删除失败");
        }
    }


    /**
     * AI-generated-content
     * tool: claude
     * version: latest
     * usage: just give the template of album, and it can generate
     * copy and add some string it not cover
     */
    private PhotoVO convertToPhotoVO(Photo photo) {
        if (photo == null) {
            return null;
        }
        PhotoVO photoVO = new PhotoVO();
        BeanUtils.copyProperties(photo, photoVO);
        try {
            if (photo.getFileUrl() != null) {
                // 如果URL不是完整的HTTP地址，添加域名前缀
                String fileUrl = photo.getFileUrl();
                if (!fileUrl.startsWith("http")) {
                    fileUrl = storageService.getFullUrl(fileUrl);
                }
                photoVO.setFileUrl(fileUrl);
            }

            if (photo.getThumbnailUrl() != null) {
                String thumbnailUrl = photo.getThumbnailUrl();
                if (!thumbnailUrl.startsWith("http")) {
                    thumbnailUrl = storageService.getFullUrl(thumbnailUrl);
                }
                photoVO.setThumbnailUrl(thumbnailUrl);
            }

            photoVO.setLocation(photo.getLocation());
            photoVO.setTag(photo.getTagName());
            photoVO.setCreatedAt(photo.getCreatedAt());
        } catch (Exception e) {
            log.warn("转换照片VO时发生错误: {}", e.getMessage());
        }

        return photoVO;
    }
}