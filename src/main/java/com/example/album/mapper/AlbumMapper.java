package com.example.album.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.album.entity.Album;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
}