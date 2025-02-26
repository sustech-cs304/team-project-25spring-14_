package com.example.album.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.album.entity.Photo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PhotoMapper extends BaseMapper<Photo> {

    /**
     * 获取相册内的所有照片
     */
    @Select("SELECT * FROM tb_photo WHERE album_id = #{albumId} ORDER BY created_at DESC")
    List<Photo> selectByAlbumId(@Param("albumId") Integer albumId);

    /**
     * 分页获取相册内的照片
     */
    @Select("SELECT * FROM tb_photo WHERE album_id = #{albumId} ORDER BY created_at DESC")
    IPage<Photo> selectPageByAlbumId(Page<Photo> page, @Param("albumId") Integer albumId);

    /**
     * 获取用户收藏的照片
     */
    @Select("SELECT * FROM tb_photo WHERE user_id = #{userId} AND is_favorite = true ORDER BY created_at DESC")
    List<Photo> selectFavoritesByUserId(@Param("userId") Integer userId);

    /**
     * 按时间范围查询照片
     */
    @Select("SELECT * FROM tb_photo WHERE user_id = #{userId} AND captured_at BETWEEN #{startTime} AND #{endTime} ORDER BY captured_at")
    List<Photo> selectByTimeRange(@Param("userId") Integer userId,
                                  @Param("startTime") LocalDateTime startTime,
                                  @Param("endTime") LocalDateTime endTime);

    /**
     * 按标签查询照片
     */
    @Select("SELECT p.* FROM tb_photo p " +
            "JOIN tb_photo_tag pt ON p.photo_id = pt.photo_id " +
            "JOIN tb_tag t ON pt.tag_id = t.tag_id " +
            "WHERE p.user_id = #{userId} AND t.name = #{tagName} " +
            "ORDER BY p.created_at DESC")
    List<Photo> selectByTag(@Param("userId") Integer userId, @Param("tagName") String tagName);
}