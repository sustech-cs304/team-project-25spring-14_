package com.example.album.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.album.entity.Album;
import com.example.album.entity.Photo;

import java.util.List;

public interface AlbumService extends IService<Album> {

    boolean createAlbum(Album album);

    boolean updateAlbum(Album album);

    boolean deleteAlbum(Integer albumId, Integer userId);

    List<Album> getAlbumsByUserId(Integer userId);

    IPage<Album> getPublicAlbums(Page<Album> page);

    boolean checkAlbumAccess(Integer albumId, Integer userId);

    boolean savePhoto(Photo photo);
}