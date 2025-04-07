package com.example.album.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.album.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    //search all tags of a photo
    @Select("SELECT t.* FROM tb_tag t " +
            "JOIN tb_photo_tag pt ON t.tag_id = pt.tag_id " +
            "WHERE pt.photo_id = #{photoId}")
    List<Tag> selectByPhotoId(@Param("photoId") Integer photoId);

    //search by tagType
    @Select("SELECT * FROM tb_tag WHERE tag_type = #{tagType}")
    List<Tag> selectByType(@Param("tagType") String tagType);

    //search the tag which user has used
    @Select("SELECT DISTINCT t.* FROM tb_tag t " +
            "JOIN tb_photo_tag pt ON t.tag_id = pt.tag_id " +
            "JOIN tb_photo p ON pt.photo_id = p.photo_id " +
            "WHERE p.user_id = #{userId}")
    List<Tag> selectUsedTagsByUserId(@Param("userId") Integer userId);
}