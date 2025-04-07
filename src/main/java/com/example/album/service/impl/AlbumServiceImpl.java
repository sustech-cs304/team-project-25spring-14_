package com.example.album.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.album.common.enums.PrivacyTypeEnum;
import com.example.album.entity.Album;
import com.example.album.entity.Photo;
import com.example.album.mapper.AlbumMapper;
import com.example.album.mapper.PhotoMapper;
//import com.example.album.service.AdminService;
import com.example.album.service.AlbumService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumServiceImpl extends ServiceImpl<AlbumMapper, Album> implements AlbumService {

    private final PhotoMapper photoMapper;

    @Override
    public boolean createAlbum(Album album) {
        album.setCreatedAt(LocalDateTime.now());
        album.setUpdatedAt(LocalDateTime.now());
        if (album.getPrivacy() == null) {
            album.setPrivacy(PrivacyTypeEnum.PRIVATE);
        }

        // 获取隐私类型的值
        String privacyValue = album.getPrivacy().getValue();

        // 使用自定义SQL语句插入数据
        int result = baseMapper.insertAlbum(album);

        return result > 0;
    }

    @Override
    public boolean updateAlbum(Album album) {
        Album existingAlbum = getById(album.getAlbumId());
        if (existingAlbum == null) {
            return false;
        }
        if (!existingAlbum.getUserId().equals(album.getUserId())) {
            return false;
        }
        album.setUpdatedAt(LocalDateTime.now());
        String privacyValue = album.getPrivacy().getValue();

        return baseMapper.updateAlbumWithPrivacy(
                album.getTitle(),
                album.getDescription(),
                privacyValue,
                album.getCoverPhotoId(),
                album.getUpdatedAt(),
                album.getAlbumId()
        ) > 0;
    }

    @Override
    @Transactional
    public boolean deleteAlbum(Integer albumId, Integer userId) {
        Album album = getById(albumId);
        if (album == null) {
            return false;
        }
        if (!album.getUserId().equals(userId)) {
            return false;
        }
        LambdaQueryWrapper<Photo> photoQueryWrapper = new LambdaQueryWrapper<>();
        photoQueryWrapper.eq(Photo::getAlbumId, albumId);
        photoMapper.delete(photoQueryWrapper);
        return removeById(albumId);
    }

    @Override
    public List<Album> getAlbumsByUserId(Integer userId) {
        LambdaQueryWrapper<Album> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Album::getUserId, userId)
                .orderByDesc(Album::getUpdatedAt);
        return list(queryWrapper);
    }


    @Override
    public IPage<Album> getPublicAlbums(Page<Album> page) {
        return baseMapper.selectPublicAlbums(page);
    }

    @Override
    public boolean checkAlbumAccess(Integer albumId, Integer userId) {
        Album album = getById(albumId);
        if (album == null) {
            return false;
        }

        if (album.getUserId().equals(userId)) {
            return true;
        }

        return PrivacyTypeEnum.PUBLIC.equals(album.getPrivacy());

        // 共享相册的逻辑在这里添加
        // TODO: 实现共享相册的访问检查
    }

    @Override
    @Transactional
    public boolean removeViolationAlbum(Integer albumId, Integer adminId, String reason) {
        Album album = getById(albumId);
        if (album == null) {
            return false;
        }

        // 添加管理员日志
//        adminService.addAdminLog(adminId, "删除违规相册", "album", albumId);

        // 删除相册内的所有照片
        LambdaQueryWrapper<Photo> photoQueryWrapper = new LambdaQueryWrapper<>();
        photoQueryWrapper.eq(Photo::getAlbumId, albumId);
        photoMapper.delete(photoQueryWrapper);

        // 删除相册
        return removeById(albumId);
    }

    @Override
    public boolean savePhoto(Photo photo) {
        return photoMapper.insert(photo) > 0;
    }
}