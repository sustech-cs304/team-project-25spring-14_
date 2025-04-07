package com.example.album.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.album.entity.PhotoAi;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PhotoAiMapper extends BaseMapper<PhotoAi> {

    /**
     * 查询包含特定对象的所有照片ID
     */
    @Select("SELECT photo_id FROM tb_photo_ai WHERE #{object} = ANY(objects)")
    List<Integer> selectPhotoIdsByObject(String object);

    /**
     * 查询包含特定人物的所有照片ID
     */
    @Select("SELECT photo_id FROM tb_photo_ai WHERE #{person} = ANY(people)")
    List<Integer> selectPhotoIdsByPerson(String person);

    /**
     * 查询特定场景的所有照片ID
     */
    @Select("SELECT photo_id FROM tb_photo_ai WHERE scene = #{scene}")
    List<Integer> selectPhotoIdsByScene(String scene);
}