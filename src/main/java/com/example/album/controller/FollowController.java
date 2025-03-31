package com.example.album.controller;

import com.example.album.entity.Result;
import com.example.album.service.FollowService;
import com.example.album.utils.ThreadLocalUtil;
import com.example.album.vo.FollowUserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/follow")
@Slf4j
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    /**
     * 关注用户
     */
    @PostMapping("/{userId}")
    public Result<Map<String, Object>> followUser(@PathVariable Integer userId) {
        try {
            log.info("接收到关注用户请求，目标用户ID: {}", userId);

            // 获取当前用户ID
            Map<String, Object> claims = ThreadLocalUtil.get();
            if (claims == null) return Result.error("未登录");
            int currentUserId = ((Number) claims.get("id")).intValue();

            // 不能关注自己
            if (currentUserId == userId) {
                return Result.error("不能关注自己");
            }

            boolean success = followService.followUser(userId);
            if (!success) {
                return Result.error("关注失败，请稍后重试");
            }

            // 构建响应数据
            Map<String, Object> response = new HashMap<>();
            response.put("followingCount", followService.getFollowingCount(currentUserId));
            response.put("message", "关注成功");

            return Result.success(response);
        } catch (Exception e) {
            log.error("关注用户失败", e);
            return Result.error("关注失败: " + e.getMessage());
        }
    }

    /**
     * 取消关注用户
     */
    @DeleteMapping("/{userId}")
    public Result<Map<String, Object>> unfollowUser(@PathVariable Integer userId) {
        try {
            log.info("接收到取消关注用户请求，目标用户ID: {}", userId);

            // 获取当前用户ID
            Map<String, Object> claims = ThreadLocalUtil.get();
            if (claims == null) return Result.error("未登录");
            int currentUserId = ((Number) claims.get("id")).intValue();

            boolean success = followService.unfollowUser(userId);
            if (!success) {
                return Result.error("取消关注失败，请稍后重试");
            }

            // 构建响应数据
            Map<String, Object> response = new HashMap<>();
            response.put("followingCount", followService.getFollowingCount(currentUserId));
            response.put("message", "取消关注成功");

            return Result.success(response);
        } catch (Exception e) {
            log.error("取消关注用户失败", e);
            return Result.error("取消关注失败");
        }
    }

    /**
     * 检查是否已关注用户
     */
    @GetMapping("/check/{userId}")
    public Result<Map<String, Object>> checkFollowStatus(@PathVariable Integer userId) {
        try {
            // 获取当前用户ID
            Map<String, Object> claims = ThreadLocalUtil.get();
            if (claims == null) return Result.error("未登录");
            int currentUserId = ((Number) claims.get("id")).intValue();

            boolean isFollowing = followService.isFollowing(currentUserId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("isFollowing", isFollowing);

            return Result.success(response);
        } catch (Exception e) {
            log.error("检查关注状态失败", e);
            return Result.error("检查关注状态失败");
        }
    }

    /**
     * 获取当前用户关注的用户列表
     */
    @GetMapping("/following")
    public Result<Map<String, Object>> getFollowingUsers() {
        try {
            // 获取当前用户ID
            Map<String, Object> claims = ThreadLocalUtil.get();
            if (claims == null) return Result.error("未登录");
            int currentUserId = ((Number) claims.get("id")).intValue();

            List<FollowUserVO> followingUsers = followService.getFollowingUsers(currentUserId);
            int followingCount = followService.getFollowingCount(currentUserId);

            Map<String, Object> response = new HashMap<>();
            response.put("users", followingUsers);
            response.put("count", followingCount);

            return Result.success(response);
        } catch (Exception e) {
            log.error("获取关注用户列表失败", e);
            return Result.error("获取关注用户列表失败");
        }
    }

    /**
     * 获取关注当前用户的用户列表（粉丝列表）
     */
    @GetMapping("/followers")
    public Result<Map<String, Object>> getFollowers() {
        try {
            // 获取当前用户ID
            Map<String, Object> claims = ThreadLocalUtil.get();
            if (claims == null) return Result.error("未登录");
            int currentUserId = ((Number) claims.get("id")).intValue();

            List<FollowUserVO> followers = followService.getFollowers(currentUserId);
            int followerCount = followService.getFollowerCount(currentUserId);

            Map<String, Object> response = new HashMap<>();
            response.put("users", followers);
            response.put("count", followerCount);

            return Result.success(response);
        } catch (Exception e) {
            log.error("获取粉丝列表失败", e);
            return Result.error("获取粉丝列表失败");
        }
    }

    /**
     * 获取指定用户关注的用户列表
     */
    @GetMapping("/{userId}/following")
    public Result<Map<String, Object>> getUserFollowing(@PathVariable Integer userId) {
        try {
            List<FollowUserVO> followingUsers = followService.getFollowingUsers(userId);
            int followingCount = followService.getFollowingCount(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("users", followingUsers);
            response.put("count", followingCount);

            return Result.success(response);
        } catch (Exception e) {
            log.error("获取用户关注列表失败", e);
            return Result.error("获取用户关注列表失败");
        }
    }

    /**
     * 获取关注指定用户的用户列表（粉丝列表）
     */
    @GetMapping("/{userId}/followers")
    public Result<Map<String, Object>> getUserFollowers(@PathVariable Integer userId) {
        try {
            List<FollowUserVO> followers = followService.getFollowers(userId);
            int followerCount = followService.getFollowerCount(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("users", followers);
            response.put("count", followerCount);

            return Result.success(response);
        } catch (Exception e) {
            log.error("获取用户粉丝列表失败", e);
            return Result.error("获取用户粉丝列表失败");
        }
    }
}