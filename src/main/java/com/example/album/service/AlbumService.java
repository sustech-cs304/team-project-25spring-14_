package com.example.album.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.album.entity.Album;

import java.util.List;

public interface AlbumService extends IService<Album> {

    /**
     * 创建相册
     */
    boolean createAlbum(Album album);

    /**
     * 更新相册
     */
    boolean updateAlbum(Album album);

    /**
     * 删除相册
     */
    boolean deleteAlbum(Integer albumId, Integer userId);

    /**
     * 获取用户的所有相册
     */
    List<Album> getAlbumsByUserId(Integer userId);


    /**
     * 获取公开的相册
     */
    IPage<Album> getPublicAlbums(Page<Album> page);

    /**
     * 检查用户是否有权限访问相册
     */
    boolean checkAlbumAccess(Integer albumId, Integer userId);

    /**
     * 管理员删除违规相册
     */
    boolean removeViolationAlbum(Integer albumId, Integer adminId, String reason);
}