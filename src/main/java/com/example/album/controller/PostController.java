package com.example.album.controller;

import com.example.album.dto.PostCreateDTO;
import com.example.album.dto.PostCreateWithPhotoDTO;
import com.example.album.dto.PostUpdateDTO;
import com.example.album.entity.Result;
import com.example.album.service.PostService;
import com.example.album.utils.ThreadLocalUtil;
import com.example.album.vo.PostVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts")
@Slf4j
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public Result<Map<String, Object>> createPost(@Valid @RequestBody PostCreateDTO createDTO) {
        try {
            Map<String, Object> claims = ThreadLocalUtil.get();
            int userId = 0;
            if (claims != null) {
                userId = ((Number) claims.get("id")).intValue();
            } else {
                return Result.error("未登录");
            }

            PostVO postVO = postService.createPost(createDTO, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("post", postVO);
            response.put("message", "帖子创建成功");

            return Result.success(response);
        } catch (Exception e) {
            log.error("创建帖子失败", e);
            return Result.error(e.getMessage());
        }
    }


    /**
     * AI-generated-content
     * tool: deepseek
     * version: latest
     * usage: give the DTO to it and ask for a method that can directly upload system photo
     * copy and add userId check
     */
    @PostMapping("/upload")
    public Result<Map<String, Object>> createPostWithPhoto(
            @Valid @ModelAttribute PostCreateWithPhotoDTO createDTO) {
        try {
            Map<String, Object> claims = ThreadLocalUtil.get();
            int userId = 0;
            if (claims != null) {
                userId = ((Number) claims.get("id")).intValue();
            } else {
                return Result.error("未登录");
            }

            if (createDTO.getPhoto() == null || createDTO.getPhoto().isEmpty()) {
                return Result.error("请至少上传一张照片");
            }

            if (createDTO.getPhoto().size() > 10) {
                return Result.error("最多可上传10张照片");
            }
            PostVO postVO = postService.createPostWithPhoto(createDTO, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("post", postVO);
            response.put("message", "帖子创建成功");

            return Result.success(response);
        } catch (Exception e) {
            log.error("创建帖子失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{postId}")
    public Result<Map<String, Object>> updatePost(
            @PathVariable Integer postId,
            @Valid @RequestBody PostUpdateDTO updateDTO) {
        try {
            Map<String, Object> claims = ThreadLocalUtil.get();
            int userId = 0;
            if (claims != null) {
                userId = ((Number) claims.get("id")).intValue();
            } else {
                return Result.error("未登录");
            }
            PostVO postVO = postService.updatePost(postId, updateDTO, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("post", postVO);
            response.put("message", "帖子更新成功");

            return Result.success(response);
        } catch (Exception e) {
            log.error("更新帖子失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除帖子
     */
    @DeleteMapping("/{postId}")
    public Result<Map<String, Object>> deletePost(@PathVariable Integer postId) {
        try {
            Map<String, Object> claims = ThreadLocalUtil.get();
            int userId = 0;
            if (claims != null) {
                userId = ((Number) claims.get("id")).intValue();
            } else {
                return Result.error("未登录");
            }
            postService.deletePost(postId, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "帖子删除成功");
            return Result.success(response);
        } catch (Exception e) {
            log.error("删除帖子失败", e);
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{postId}")
    public Result<Map<String, Object>> getPostDetail(@PathVariable Integer postId) {
        try {
            Map<String, Object> claims = ThreadLocalUtil.get();
            Integer userId = null;
            if (claims != null) {
                userId = ((Number) claims.get("id")).intValue();
            }

            PostVO postVO = postService.getPostById(postId, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("post", postVO);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取帖子详情失败", e);
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public Result<Map<String, Object>> getUserPosts(@PathVariable Integer userId) {
        try {
            Map<String, Object> claims = ThreadLocalUtil.get();
            Integer currentUserId = null;
            if (claims != null) {
                currentUserId = ((Number) claims.get("id")).intValue();
            }
            List<PostVO> posts = postService.getPostsByUserId(userId, currentUserId);
            Map<String, Object> response = new HashMap<>();
            response.put("posts", posts);
            response.put("count", posts.size());
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取用户帖子失败", e);
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/public")
    public Result<Map<String, Object>> getPublicPosts() {
        try {
            Map<String, Object> claims = ThreadLocalUtil.get();
            Integer userId = null;
            if (claims != null) {
                userId = ((Number) claims.get("id")).intValue();
            }

            List<PostVO> posts = postService.getPublicPosts(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("posts", posts);
            response.put("count", posts.size());
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取公开帖子失败", e);
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/following")
    public Result<Map<String, Object>> getFollowingPosts() {
        try {
            Map<String, Object> claims = ThreadLocalUtil.get();
            int userId = 0;
            if (claims != null) {
                userId = ((Number) claims.get("id")).intValue();
            } else {
                return Result.error("未登录");
            }

            List<PostVO> posts = postService.getFollowingPosts(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("posts", posts);
            response.put("count", posts.size());

            return Result.success(response);
        } catch (Exception e) {
            log.error("获取关注动态失败", e);
            return Result.error(e.getMessage());
        }
    }
}