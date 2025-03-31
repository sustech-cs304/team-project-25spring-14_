package com.example.album.service;

import com.example.album.entity.Follow;
import com.example.album.vo.FollowUserVO;

import java.util.List;

public interface FollowService {

    /**
     * 关注用户
     * @param followingId 被关注用户ID
     * @return 是否成功关注
     */
    boolean followUser(Integer followingId);

    /**
     * 取消关注用户
     * @param followingId 被取消关注的用户ID
     * @return 是否成功取消关注
     */
    boolean unfollowUser(Integer followingId);

    /**
     * 检查是否已关注某用户
     * @param followerId 关注者ID
     * @param followingId 被关注者ID
     * @return 是否已关注
     */
    boolean isFollowing(Integer followerId, Integer followingId);

    /**
     * 获取用户关注的所有用户
     * @param userId 用户ID
     * @return 关注的用户列表
     */
    List<FollowUserVO> getFollowingUsers(Integer userId);

    /**
     * 获取关注某用户的所有用户
     * @param userId 用户ID
     * @return 粉丝用户列表
     */
    List<FollowUserVO> getFollowers(Integer userId);

    /**
     * 获取用户关注数
     * @param userId 用户ID
     * @return 关注数
     */
    int getFollowingCount(Integer userId);

    /**
     * 获取用户粉丝数
     * @param userId 用户ID
     * @return 粉丝数
     */
    int getFollowerCount(Integer userId);
}