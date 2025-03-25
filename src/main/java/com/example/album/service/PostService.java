package com.example.album.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.album.dto.PostCreateDTO;
import com.example.album.dto.PostUpdateDTO;
import com.example.album.vo.PostVO;

import java.util.List;

public interface PostService {
    /**
     * 创建帖子
     */
    PostVO createPost(PostCreateDTO createDTO, Integer userId);

    /**
     * 更新帖子
     */
    PostVO updatePost(Integer postId, PostUpdateDTO updateDTO, Integer userId);

    /**
     * 删除帖子
     */
    void deletePost(Integer postId, Integer userId);

    /**
     * 获取帖子详情
     */
    PostVO getPostById(Integer postId, Integer currentUserId);

    /**
     * 获取用户的所有帖子
     */
    List<PostVO> getPostsByUserId(Integer userId, Integer currentUserId);

    /**
     * 获取公开的帖子
     */
    List<PostVO> getPublicPosts(Integer currentUserId);

    /**
     * 获取关注用户的帖子
     */
    List<PostVO> getFollowingPosts(Integer userId);
}