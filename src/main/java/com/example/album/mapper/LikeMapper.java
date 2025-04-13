package com.example.album.mapper;

import org.apache.ibatis.annotations.*;

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

    /**
     * 增加点赞数
     *
     * @param postId 帖子ID
     */
    @Update("UPDATE tb_post SET like_count = like_count + 1 WHERE post_id = #{postId}")
    void addLikeCount(Integer postId);

    /**
     * 减少点赞数
     *
     * @param postId 帖子ID
     */
    @Update("UPDATE tb_post SET like_count = like_count - 1 WHERE post_id = #{postId}")
    void deleteLikeCount(Integer postId);

    /**
     * 判断用户是否点赞
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return true: 已点赞, false: 未点赞
     */
    @Select("SELECT COUNT(*) > 0 FROM tb_like WHERE post_id = #{postId} AND user_id = #{userId}")
    boolean isLiked(Integer postId, Integer userId);
}
