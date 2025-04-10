package com.example.album.controller;

import com.example.album.entity.Result;
import com.example.album.service.LikeService;
import com.example.album.utils.ThreadLocalUtil;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping("/like/{postId}")
    public Result likePost(@PathVariable Integer postId) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userId = (Integer) claims.get("userId");
        boolean result = likeService.likePost(postId, userId);
        return Result.success(result);
    }

    @Delete("/unlike/{postId}")
    public Result unlikePost(@PathVariable Integer postId) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userId = (Integer) claims.get("userId");
        boolean result = likeService.unlikePost(postId, userId);
        return Result.success(result);
    }
}