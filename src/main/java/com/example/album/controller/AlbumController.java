package com.example.album.controller;

import com.example.album.dto.PhotoStorageResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.album.dto.AlbumCreateDTO;
import com.example.album.dto.AlbumUpdateDTO;
import com.example.album.dto.AlbumCoverUpdateDTO;
import com.example.album.entity.Album;
import com.example.album.entity.Photo;
import com.example.album.entity.Result;
import com.example.album.entity.User;
import com.example.album.mapper.UserMapper;
import com.example.album.service.AlbumService;
import com.example.album.service.StorageService;
import com.example.album.utils.ThreadLocalUtil;
import com.example.album.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final UserMapper userMapper;
    private final AlbumService albumService;
    private final StorageService storageService;

    /**
     * AI-generated-content
     * tool: claude
     * version: latest
     * usage: ask it to give me a template that can give me the log and how to write the parameter
     * just add log and correct the annotation, result use the same format that defined by other teammate
     */
    @PostMapping("/upload")
    public Result<Map<String, Object>> createAlbum(
            @Valid @ModelAttribute AlbumCreateDTO createDTO) {

        try {
            Map<String, Object> claims = ThreadLocalUtil.get();
            int userId = 0;
            if (claims != null) {
                userId = ((Number) claims.get("id")).intValue();
                log.info("从ThreadLocal获取的用户ID: {}", userId);
            } else return null;

            log.info("接收到创建相册请求，用户ID: {}, 相册标题: {}", userId, createDTO.getTitle());

            Album album = new Album();
            BeanUtils.copyProperties(createDTO, album);
            album.setUserId(userId);
            album.setCreatedAt(LocalDateTime.now());
            album.setUpdatedAt(LocalDateTime.now());

            boolean result = albumService.createAlbum(album);
            if (!result) {
                throw new Exception("创建相册失败");
            }

            AlbumVO albumVO = convertToAlbumVO(album);

            Map<String, Object> response = new HashMap<>();
            response.put("album", albumVO);
            response.put("message", "相册创建成功");

            return Result.success(response);
        } catch (Exception e) {
            log.error("创建相册失败", e);
            return Result.error("创建相册失败");
        }
    }

    @GetMapping("/{albumId}")
    public Result<Map<String, Object>> getAlbumDetail(
            @PathVariable Integer albumId) {

        try {
            Map<String, Object> claims = ThreadLocalUtil.get();
            int userId = 0;
            if (claims != null) {
                userId = ((Number) claims.get("id")).intValue();
                log.info("从ThreadLocal获取的用户ID: {}", userId);
            } else return null;

            if (!albumService.checkAlbumAccess(albumId, userId)) {
                throw new Exception("没有访问权限");
            }
            Album album = albumService.getById(albumId);
            if (album == null) {
                throw new Exception("相册不存在");
            }
            AlbumDetailVO albumDetailVO = convertToAlbumDetailVO(album);
            Map<String, Object> response = new HashMap<>();
            response.put("album", albumDetailVO);

            return Result.success(response);
        } catch (Exception e) {
            log.error("获取相册详情失败", e);
            return Result.error("获取相册详情失败");
        }
    }

    @PutMapping("/{albumId}")
    public Result<Map<String, Object>> updateAlbum(
            @PathVariable Integer albumId,
            @Valid @ModelAttribute AlbumUpdateDTO updateDTO) {

        try {
            Map<String, Object> claims = ThreadLocalUtil.get();
            int userId = 0;
            if (claims != null) {
                userId = ((Number) claims.get("id")).intValue();
                log.info("从ThreadLocal获取的用户ID: {}", userId);
            } else return null;

            Album album = albumService.getById(albumId);
            if (album == null) {
                throw new Exception("没有访问权限");
            }
            if (!album.getUserId().equals(userId)) {
                throw new Exception("没有修改权限");
            }

            boolean hasUpdates = false;
            if (updateDTO.getTitle() != null) {
                album.setTitle(updateDTO.getTitle());
                hasUpdates = true;
            }
            if (updateDTO.getDescription() != null) {
                album.setDescription(updateDTO.getDescription());
                hasUpdates = true;
            }
            if (updateDTO.getPrivacy() != null) {
                album.setPrivacy(updateDTO.getPrivacy());
                hasUpdates = true;
            }
            if (updateDTO.getCoverPhotoId() != null) {
                album.setCoverPhotoId(updateDTO.getCoverPhotoId());
                hasUpdates = true;
            }

            if (hasUpdates) {
                album.setUpdatedAt(LocalDateTime.now());
                boolean result = albumService.updateAlbum(album);
                if (!result) {
                    throw new Exception("更新相册失败");
                }
            }

            AlbumVO albumVO = convertToAlbumVO(album);
            Map<String, Object> response = new HashMap<>();
            response.put("album", albumVO);
            response.put("message", "相册更新成功");

            return Result.success(response);
        } catch (Exception e) {
            log.error("更新相册失败", e);

            return Result.error("更新相册失败");
        }
    }

    @DeleteMapping("/{albumId}")
    public Result<Map<String, Object>> deleteAlbum(@PathVariable Integer albumId) {
        log.info("接收到删除相册请求，相册ID: {}", albumId);

        try {
            // 从ThreadLocal获取用户信息
            Map<String, Object> claims = ThreadLocalUtil.get();
            if (claims == null) {
                return Result.error("未登录");
            }

            Integer currentUserId = ((Number) claims.get("id")).intValue();
            String role = (String) claims.get("role");
            log.info("从ThreadLocal获取的用户ID: {}, 角色: {}", currentUserId, role);

            Album album = albumService.getById(albumId);
            if (album == null) {
                throw new Exception("相册不存在");
            }

            // 验证权限：自己的相册可以删除，管理员可以删除公开相册
            boolean hasPermission = false;

            // 如果是相册所有者
            if (album.getUserId().equals(currentUserId)) {
                hasPermission = true;
            }
            // 如果是管理员且相册是公开的
            else if ("admin".equals(role) && "public".equals(album.getPrivacy().getValue())) {
                hasPermission = true;
            }

            if (!hasPermission) {
                log.warn("用户 {} 尝试删除相册 {}, 权限不足", currentUserId, albumId);
                throw new Exception("无权删除此相册");
            }
            boolean result = albumService.deleteAlbum(albumId,currentUserId,hasPermission);
            if (!result) {
                throw new Exception("删除相册失败");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "相册删除成功");

            return Result.success(response);
        } catch (Exception e) {
            log.error("删除相册失败", e);
            return Result.error("删除相册失败：" + e.getMessage());
        }
    }

    
    @GetMapping("/user/{userId}")
    public Result<Map<String, Object>> getUserAlbums(
            @RequestParam(value = "currentUserId", required = false) Integer currentUserId) {

        try {

            Map<String, Object> claims = ThreadLocalUtil.get();
            int userId = 0;
            if (claims != null) {
                userId = ((Number) claims.get("id")).intValue();
                log.info("从ThreadLocal获取的用户ID: {}", userId);
            } else return null;

            List<Album> albums = albumService.getAlbumsByUserId(currentUserId);

            List<AlbumVO> albumVOList = new ArrayList<>();
            for (Album album : albums) {
                if (album.getPrivacy().getValue().equals("public")|| currentUserId == userId) {
                    AlbumVO albumVO = convertToAlbumVO(album);
                    albumVOList.add(albumVO);
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("albums", albumVOList);
            response.put("count", albumVOList.size());

            return Result.success(response);
        } catch (Exception e) {
            log.error("获取用户相册失败", e);

            return Result.error("获取用户相册失败");
        }
    }

    @GetMapping("/public")
    public Result<Map<String, Object>> getPublicAlbums(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {

        try {
            Page<Album> page = new Page<>(current, size);
            IPage<Album> albumPage = albumService.getPublicAlbums(page);
            List<AlbumVO> albumVOList = albumPage.getRecords().stream()
                    .map(this::convertToAlbumVO)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("current", albumPage.getCurrent());
            response.put("size", albumPage.getSize());
            response.put("total", albumPage.getTotal());
            response.put("records", albumVOList);

            return Result.success(response);
        } catch (Exception e) {
            log.error("获取公开相册失败", e);
            return Result.error("获取公开相册失败");
        }
    }


    /**
     * AI-generated-content
     * tool: claude
     * version: latest
     * usage: ask how to do it in java's stream that in high efficiency
     * copy directly
     */
    @GetMapping("/recent")
    public Result<Map<String, Object>> getRecentAlbums(
            @RequestParam(defaultValue = "5") Integer limit) {

        try {
            // 从ThreadLocal获取当前用户ID
            Map<String, Object> claims = ThreadLocalUtil.get();
            if (claims == null) {
                return Result.error("未登录");
            }

            int userId = ((Number) claims.get("id")).intValue();
            String role = (String) claims.get("role");
            log.info("从ThreadLocal获取的用户ID: {}, 角色: {}", userId, role);

            List<Album> albums = albumService.getAlbumsByUserId(userId);

            // 按更新时间排序并限制数量（用户自己可以看到所有相册）
            List<AlbumVO> albumVOList = albums.stream()
                    .sorted((a1, a2) -> a2.getUpdatedAt().compareTo(a1.getUpdatedAt()))
                    .limit(limit)
                    .map(this::convertToAlbumVO)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("albums", albumVOList);
            response.put("count", albumVOList.size());

            return Result.success(response);
        } catch (Exception e) {
            log.error("获取最近相册失败", e);
            return Result.error("获取最近相册失败");
        }
    }

    @PostMapping("/{albumId}/cover/upload")
    public Result<Map<String, Object>> updateCoverWithPhoto(
            @PathVariable Integer albumId,
            @Valid @ModelAttribute AlbumCoverUpdateDTO updateDTO) {

        try {
            log.info("接收到更新相册封面请求，相册ID: {}", albumId);
            Map<String, Object> claims = ThreadLocalUtil.get();
            int userId = 0;
            if (claims != null) {
                userId = ((Number) claims.get("id")).intValue();
                log.info("从ThreadLocal获取的用户ID: {}", userId);
            } else {
                return Result.error("未登录");
            }
            Album album = albumService.getById(albumId);
            if (album == null) {
                throw new Exception("相册不存在");
            }

            if (!album.getUserId().equals(userId)) {
                throw new Exception("没有修改权限");
            }

            PhotoStorageResult storageResult = storageService.storePhoto(updateDTO.getPhoto(), 0);

            Photo photo = new Photo();
            photo.setUserId(0);
            photo.setAlbumId(0);
            photo.setFileName(storageResult.getOriginalFilename());
            photo.setFileUrl(storageResult.getFileUrl());
            photo.setThumbnailUrl(storageResult.getThumbnailUrl());
            photo.setCapturedAt(storageResult.getCapturedAt() != null ?
                    storageResult.getCapturedAt() : LocalDateTime.now());
            photo.setCreatedAt(LocalDateTime.now());
            photo.setIsFavorite(false);

            photo.setLocation(storageResult.getLocation());
            photo.setTagName(storageResult.getTag());


            boolean saved = albumService.savePhoto(photo);
            if (!saved) {
                throw new Exception("保存封面照片失败");
            }

            album.setCoverPhotoId(photo.getPhotoId());
            album.setUpdatedAt(LocalDateTime.now());
            boolean updated = albumService.updateAlbum(album);
            if (!updated) {
                throw new Exception("更新相册封面失败");
            }
            AlbumVO albumVO = convertToAlbumVO(album);

            Map<String, Object> response = new HashMap<>();
            response.put("album", albumVO);
            response.put("message", "相册封面更新成功");

            return Result.success(response);
        } catch (Exception e) {
            log.error("更新相册封面失败", e);
            return Result.error("更新相册封面失败：" + e.getMessage());
        }
    }

    /**
     * AI-generated-content
     * tool: claude
     * version: latest
     * usage: first time using VO and DTO, and sak for example
     * use the format and add some corner case it doesn't cover
     */
    private AlbumVO convertToAlbumVO(Album album) {
        if (album == null) {
            return null;
        }

        AlbumVO albumVO = new AlbumVO();
        BeanUtils.copyProperties(album, albumVO);

        // 获取相册中的照片
        List<Photo> photos = storageService.getPhotosByAlbumId(album.getAlbumId());

        // 设置照片数量
        albumVO.setPhotoCount(photos.size());
        albumVO.setDescription(album.getDescription());
        albumVO.setPrivacy(album.getPrivacy());
        return albumVO;
    }

    /**
     * AI-generated-content
     * tool: claude
     * version: latest
     * usage: first time using VO and DTO, and sak for example
     * use the format and add some corner case it doesn't cover
     */
    private AlbumDetailVO convertToAlbumDetailVO(Album album) {
        AlbumDetailVO albumDetailVO = new AlbumDetailVO();
        BeanUtils.copyProperties(album, albumDetailVO);

        // 获取相册中的照片
        List<Photo> photos = storageService.getPhotosByAlbumId(album.getAlbumId());

        // 设置照片数量
        albumDetailVO.setPhotoCount(photos.size());

        // 获取最新的几张照片
        List<PhotoVO> latestPhotos = photos.stream()
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .limit(5)
                .map(photo -> {
                    PhotoVO photoVO = new PhotoVO();
                    BeanUtils.copyProperties(photo, photoVO);
                    return photoVO;
                })
                .collect(Collectors.toList());

        albumDetailVO.setLatestPhotos(latestPhotos);


        // 设置用户信息
        UserVO userVO = new UserVO(); // 这里应该从用户服务获取用户信息
        User user = userMapper.selectById(album.getUserId());
        if (user != null) {
            BeanUtils.copyProperties(user, userVO);
            albumDetailVO.setUser(userVO);
        } else {
            // 如果 user 为空，可以设置为 null 或者默认值
            albumDetailVO.setUser(null);
        }
        albumDetailVO.setUser(userVO);

        return albumDetailVO;
    }
}