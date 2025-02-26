package com.example.album.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.album.entity.PhotoTag;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PhotoTagMapper extends BaseMapper<PhotoTag> {

    // search
    @Select("SELECT photo_id FROM tb_photo_tag WHERE tag_id = #{tagId}")
    List<Integer> selectPhotoIdsByTagId(@Param("tagId") Integer tagId);

    // DELETE tagid $ phtoid's relationship
    @Delete("DELETE FROM tb_photo_tag WHERE photo_id = #{photoId} AND tag_id = #{tagId}")
    int deleteByPhotoIdAndTagId(@Param("photoId") Integer photoId, @Param("tagId") Integer tagId);

    // DELETE photo_tag
    @Delete("DELETE FROM tb_photo_tag WHERE photo_id = #{photoId}")
    int deleteByPhotoId(@Param("photoId") Integer photoId);

    // INSERT photo_tag
    @Insert("INSERT INTO tb_photo_tag(photo_id, tag_id) VALUES(#{photoId}, #{tagId})")
    int insertPhotoTag(@Param("photoId") Integer photoId, @Param("tagId") Integer tagId);
}