package com.example.album.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.album.entity.Follow;
import com.example.album.vo.FollowUserVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FollowMapper extends BaseMapper<Follow> {

    /**
     * 检查是否存在关注关系
     */
    @Select("SELECT COUNT(*) FROM tb_follow WHERE follower_id = #{followerId} AND following_id = #{followingId}")
    int checkFollowExists(@Param("followerId") Integer followerId, @Param("followingId") Integer followingId);

    /**
     * 获取用户关注的所有用户ID
     */
    @Select("SELECT following_id FROM tb_follow WHERE follower_id = #{userId}")
    List<Integer> getFollowingIds(@Param("userId") Integer userId);

    /**
     * 获取关注某用户的所有用户ID
     */
    @Select("SELECT follower_id FROM tb_follow WHERE following_id = #{userId}")
    List<Integer> getFollowerIds(@Param("userId") Integer userId);

    /**
     * 获取用户关注数量
     */
    @Select("SELECT COUNT(*) FROM tb_follow WHERE follower_id = #{userId}")
    int countFollowing(@Param("userId") Integer userId);

    /**
     * 获取用户粉丝数量
     */
    @Select("SELECT COUNT(*) FROM tb_follow WHERE following_id = #{userId}")
    int countFollowers(@Param("userId") Integer userId);

    /**
     * 添加关注关系
     */
    @Insert("INSERT INTO tb_follow(follower_id, following_id, created_at) VALUES(#{followerId}, #{followingId}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "followId")
    int addFollow(Follow follow);

    /**
     * 取消关注关系
     */
    @Delete("DELETE FROM tb_follow WHERE follower_id = #{followerId} AND following_id = #{followingId}")
    int removeFollow(@Param("followerId") Integer followerId, @Param("followingId") Integer followingId);

    /**
     * 获取用户关注的所有用户详细信息
     */
    @Select("SELECT u.user_id, u.username, u.avatar_url, f.created_at AS follow_time " +
            "FROM tb_follow f JOIN tb_user u ON f.following_id = u.user_id " +
            "WHERE f.follower_id = #{userId} ORDER BY f.created_at DESC")
    List<FollowUserVO> getFollowingUsers(@Param("userId") Integer userId);

    /**
     * 获取关注某用户的所有用户详细信息
     */
    @Select("SELECT u.user_id, u.username, u.avatar_url, f.created_at AS follow_time " +
            "FROM tb_follow f JOIN tb_user u ON f.follower_id = u.user_id " +
            "WHERE f.following_id = #{userId} ORDER BY f.created_at DESC")
    List<FollowUserVO> getFollowers(@Param("userId") Integer userId);
}