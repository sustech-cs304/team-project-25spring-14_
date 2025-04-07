package com.example.album.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.album.entity.Post;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PostMapper extends BaseMapper<Post> {
    @Insert("INSERT INTO tb_post(user_id,  caption, privacy, created_at, updated_at) " +
            "VALUES(#{userId}, #{caption}, #{privacy}::privacy_type, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "postId")
    int insert(Post post);

    /**
     * 获取用户的所有帖子
     */
    @Select("SELECT * FROM tb_post WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Post> selectByUserId(@Param("userId") Integer userId);

    /**
     * 分页获取用户的帖子
     */
    @Select("SELECT * FROM tb_post WHERE user_id = #{userId} ORDER BY created_at DESC")
    IPage<Post> selectPageByUserId(Page<Post> page, @Param("userId") Integer userId);

    /**
     * 获取公开的帖子
     */
    @Select("SELECT * FROM tb_post WHERE privacy = 'public'::privacy_type ORDER BY created_at DESC")
    List<Post> selectPublicPosts();

    /**
     * 分页获取公开的帖子
     */
    @Select("SELECT * FROM tb_post WHERE privacy = 'public'::privacy_type ORDER BY created_at DESC")
    IPage<Post> selectPagePublicPosts(Page<Post> page);

    /**
     * 更新帖子
     */
    @Update("UPDATE tb_post SET caption = #{caption}, privacy = #{privacy}::privacy_type, " +
            "updated_at = #{updatedAt} WHERE post_id = #{postId}")
    int updatePost(Post post);

    /**
     * 获取关注用户的帖子
     */
    @Select("SELECT p.* FROM tb_post p " +
            "JOIN tb_follow f ON p.user_id = f.followed_id " +
            "WHERE f.follower_id = #{userId} ORDER BY p.created_at DESC")
    List<Post> selectFollowingPosts(@Param("userId") Integer userId);


}