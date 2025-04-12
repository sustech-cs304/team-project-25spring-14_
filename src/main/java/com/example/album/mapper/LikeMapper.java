package com.example.album.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LikeMapper {

    /**
     * 点赞
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     */
    @Insert("INSERT INTO tb_like (post_id, user_id, created_at) VALUES (#{postId}, #{userId}, NOW())")
    void likePost(Integer postId, Integer userId);

    /**
     * 取消点赞
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     */
    @Delete("DELETE FROM tb_like WHERE post_id = #{postId} AND user_id = #{userId}")
    void unLikePost(Integer postId, Integer userId);
}
