package com.example.album.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.album.entity.Album;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AlbumMapper extends BaseMapper<Album> {

    /**
     * 获取用户的所有相册
     */
    @Select("SELECT * FROM tb_album WHERE user_id = #{userId} ORDER BY updated_at DESC")
    List<Album> selectByUserId(@Param("userId") Integer userId);

    /**
     * 分页获取用户的相册
     */
    @Select("SELECT * FROM tb_album WHERE user_id = #{userId} ORDER BY updated_at DESC")
    IPage<Album> selectPageByUserId(Page<Album> page, @Param("userId") Integer userId);

    /**
     * 获取公开的相册
     */
    @Select("SELECT * FROM tb_album WHERE privacy = 'public' ORDER BY updated_at DESC")
    IPage<Album> selectPublicAlbums(Page<Album> page);

    @Insert("INSERT INTO tb_album (user_id, title, description, privacy, created_at, updated_at, cover_photo_id) " +
            "VALUES (#{userId}, #{title}, #{description}, #{privacy}::privacy_type, #{createdAt}, #{updatedAt}, #{coverPhotoId})")
    @Options(useGeneratedKeys = true, keyProperty = "albumId")
    int insertAlbum(Album album);
    
    @Update("UPDATE tb_album SET title = #{title}, description = #{description}, " +
            "privacy = #{privacy}::privacy_type, cover_photo_id = #{coverPhotoId}, " +
            "updated_at = #{updatedAt} WHERE album_id = #{albumId}")
    int updateAlbumWithPrivacy(String title, String description, String privacy,
                               Integer coverPhotoId, LocalDateTime updatedAt, Integer albumId);
}