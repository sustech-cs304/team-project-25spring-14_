package com.example.album.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.album.dto.AlbumCreateDTO;
import com.example.album.dto.AlbumUpdateDTO;
import com.example.album.entity.Album;
import com.example.album.entity.Photo;
import com.example.album.entity.Result;
import com.example.album.entity.User;
import com.example.album.mapper.UserMapper;
import com.example.album.service.AlbumService;
import com.example.album.service.StorageService;
import com.example.album.vo.AlbumDetailVO;
import com.example.album.vo.AlbumVO;
import com.example.album.vo.PhotoVO;
import com.example.album.vo.UserVO;
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
     * 创建相册
     */
    @PostMapping("/upload")
    public Result<Map<String, Object>> createAlbum(
            @RequestParam("userId") Integer userId,
            @Valid @ModelAttribute AlbumCreateDTO createDTO) {

        log.info("接收到创建相册请求，用户ID: {}, 相册标题: {}", userId, createDTO.getTitle());

        try {
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

    /**
     * 获取相册详情
     */
    @GetMapping("/{albumId}")
    public Result<Map<String, Object>> getAlbumDetail(
            @PathVariable Integer albumId,
            @RequestParam(value = "userId", required = false) Integer userId) {

        try {
            // 检查访问权限
            if (!albumService.checkAlbumAccess(albumId, userId)) {
                throw new Exception("没有访问权限");
            }

            Album album = albumService.getById(albumId);
            if (album == null) {
                throw new Exception("相册不存在");
            }

            // 构建详细响应
            AlbumDetailVO albumDetailVO = convertToAlbumDetailVO(album);

            Map<String, Object> response = new HashMap<>();
            response.put("album", albumDetailVO);

            return Result.success(response);
        } catch (Exception e) {
            log.error("获取相册详情失败", e);
            return Result.error("获取相册详情失败");
        }
    }

    /**
     * 更新相册
     */
    @PutMapping("/{albumId}")
    public Result<Map<String, Object>> updateAlbum(
            @PathVariable Integer albumId,
            @RequestParam("userId") Integer userId,
            @Valid @ModelAttribute AlbumUpdateDTO updateDTO) {

        try {
            Album album = albumService.getById(albumId);
            if (album == null) {
                throw new Exception("没有访问权限");
            }

            // 检查是否为相册所有者
            if (!album.getUserId().equals(userId)) {
                throw new Exception("没有修改权限");
            }

            boolean hasUpdates = false;

            // 只更新非空字段
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

            // 返回更新后的相册信息
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

    /**
     * 删除相册
     */
    @DeleteMapping("/{albumId}")
    public Result<Map<String, Object>> deleteAlbum(
            @PathVariable Integer albumId,
            @RequestParam("userId") Integer userId) {

        log.info("接收到删除相册请求，相册ID: {}, 用户ID: {}", albumId, userId);

        try {
            boolean result = albumService.deleteAlbum(albumId, userId);
            if (!result) {
                throw new Exception("删除相册失败");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "相册删除成功");

            return Result.success(response);
        } catch (Exception e) {
            log.error("删除相册失败", e);
            return Result.error("删除相册失败");
        }
    }

    /**
     * 获取用户的相册列表
     */
    @GetMapping("/user/{userId}")
    public Result<Map<String, Object>> getUserAlbums(
            @PathVariable Integer userId,
            @RequestParam(value = "currentUserId", required = false) Integer currentUserId) {

        try {
            List<Album> albums = albumService.getAlbumsByUserId(userId);

            // 转换为VO
            List<AlbumVO> albumVOList = new ArrayList<>();
            for (Album album : albums) {
                // 检查访问权限
                if (userId.equals(currentUserId) || album.getPrivacy().getValue().equals("public")) {
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

    /**
     * 分页获取公开相册
     */
    @GetMapping("/public")
    public Result<Map<String, Object>> getPublicAlbums(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {

        try {
            Page<Album> page = new Page<>(current, size);
            IPage<Album> albumPage = albumService.getPublicAlbums(page);

            // 转换为VO
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
     * 获取用户最近更新的相册
     */
    @GetMapping("/recent/{userId}")
    public Result<Map<String, Object>> getRecentAlbums(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "5") Integer limit,
            @RequestParam(value = "currentUserId", required = false) Integer currentUserId) {

        try {
            // 检查访问权限
            boolean isSelf = userId.equals(currentUserId);

            List<Album> albums = albumService.getAlbumsByUserId(userId);

            // 按更新时间排序并限制数量
            List<AlbumVO> albumVOList = albums.stream()
                    .filter(album -> isSelf || album.getPrivacy().getValue().equals("public"))
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

    /**
     * 将Album实体转换为AlbumVO
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

        try {
            if (album.getCoverPhotoId() != null) {
                Photo coverPhoto = storageService.getById(album.getCoverPhotoId());
                if (coverPhoto != null) {
                    String thumbnailUrl = coverPhoto.getThumbnailUrl();
                    // 确保URL是完整的
                    if (thumbnailUrl != null && !thumbnailUrl.startsWith("http")) {
                        thumbnailUrl = storageService.getFullUrl(thumbnailUrl);
                    }
                    albumVO.setCoverPhotoUrl(thumbnailUrl);
                }
            }

        } catch (Exception e) {
            log.warn("转换相册VO时发生错误: {}", e.getMessage());
        }

        return albumVO;
    }

    /**
     * 将Album实体转换为AlbumDetailVO（包含更多详细信息）
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

        // 设置封面照片URL
        if (album.getCoverPhotoId() != null) {
            Photo coverPhoto = storageService.getById(album.getCoverPhotoId());
            if (coverPhoto != null) {
                albumDetailVO.setCoverPhotoUrl(coverPhoto.getThumbnailUrl());
            }
        }

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