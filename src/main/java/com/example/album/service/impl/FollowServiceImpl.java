package com.example.album.service.impl;

import com.example.album.entity.Follow;
import com.example.album.entity.User;
import com.example.album.mapper.FollowMapper;
import com.example.album.mapper.UserMapper;
import com.example.album.service.FollowService;
import com.example.album.utils.ThreadLocalUtil;
import com.example.album.vo.FollowUserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowMapper followMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public boolean followUser(Integer followingId) {
        // 获取当前用户
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer currentUserId = ((Number) claims.get("id")).intValue();

        // 验证用户
        User followingUser = userMapper.selectById(followingId);
        if (followingUser == null) {
            log.warn("尝试关注不存在的用户：{}", followingId);
            return false;
        }

        // 不能关注自己
        if (currentUserId.equals(followingId)) {
            log.warn("用户 {} 尝试关注自己", currentUserId);
            return false;
        }

        // 检查是否已关注
        if (isFollowing(currentUserId, followingId)) {
            log.info("用户 {} 已经关注用户 {}", currentUserId, followingId);
            return true;
        }

        // 创建关注关系
        Follow follow = new Follow();
        follow.setFollowerId(currentUserId);
        follow.setFollowingId(followingId);
        follow.setCreatedAt(LocalDateTime.now());

        int result = followMapper.addFollow(follow);
        if (result > 0) {
            log.info("用户 {} 成功关注用户 {}", currentUserId, followingId);
            return true;
        } else {
            log.error("用户 {} 关注用户 {} 失败", currentUserId, followingId);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean unfollowUser(Integer followingId) {
        // 获取当前用户
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer currentUserId = ((Number) claims.get("id")).intValue();

        // 检查是否已关注
        if (!isFollowing(currentUserId, followingId)) {
            log.info("用户 {} 未关注用户 {}, 无需取消关注", currentUserId, followingId);
            return true;
        }

        int result = followMapper.removeFollow(currentUserId, followingId);
        if (result > 0) {
            log.info("用户 {} 成功取消关注用户 {}", currentUserId, followingId);
            return true;
        } else {
            log.error("用户 {} 取消关注用户 {} 失败", currentUserId, followingId);
            return false;
        }
    }

    @Override
    public boolean isFollowing(Integer followerId, Integer followingId) {
        return followMapper.checkFollowExists(followerId, followingId) > 0;
    }

    @Override
    public List<FollowUserVO> getFollowingUsers(Integer userId) {
        List<FollowUserVO> followingUsers = followMapper.getFollowingUsers(userId);

        // 获取当前用户是否已关注这些用户
        Map<String, Object> claims = ThreadLocalUtil.get();
        if (claims != null) {
            Integer currentUserId = ((Number) claims.get("id")).intValue();

            // 查询当前用户已关注的用户ID列表
            List<Integer> followingIds = followMapper.getFollowingIds(currentUserId);

            // 标记当前用户是否已关注这些用户
            followingUsers.forEach(user ->
                    user.setIsFollowing(followingIds.contains(user.getUserId())));
        }

        return followingUsers;
    }

    @Override
    public List<FollowUserVO> getFollowers(Integer userId) {
        List<FollowUserVO> followers = followMapper.getFollowers(userId);

        // 获取当前用户是否已关注这些用户
        Map<String, Object> claims = ThreadLocalUtil.get();
        if (claims != null) {
            Integer currentUserId = ((Number) claims.get("id")).intValue();

            // 查询当前用户已关注的用户ID列表
            List<Integer> followingIds = followMapper.getFollowingIds(currentUserId);

            // 标记当前用户是否已关注这些用户
            followers.forEach(user ->
                    user.setIsFollowing(followingIds.contains(user.getUserId())));
        }

        return followers;
    }

    @Override
    public int getFollowingCount(Integer userId) {
        return followMapper.countFollowing(userId);
    }

    @Override
    public int getFollowerCount(Integer userId) {
        return followMapper.countFollowers(userId);
    }
}