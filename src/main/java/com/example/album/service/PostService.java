package com.example.album.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.album.dto.PostCreateDTO;
import com.example.album.dto.PostCreateWithPhotoDTO;
import com.example.album.dto.PostUpdateDTO;
import com.example.album.vo.PostVO;

import java.util.List;

public interface PostService {

    PostVO createPost(PostCreateDTO createDTO, Integer userId);

    PostVO createPostWithPhoto(PostCreateWithPhotoDTO createDTO, Integer userId);

    PostVO updatePost(Integer postId, PostUpdateDTO updateDTO, Integer userId);

    void deletePost(Integer postId, Integer userId);

    PostVO getPostById(Integer postId, Integer currentUserId);

    List<PostVO> getPostsByUserId(Integer userId, Integer currentUserId);

    List<PostVO> getPublicPosts(Integer currentUserId);

    List<PostVO> getFollowingPosts(Integer userId);

    int getLikeCount(Integer postId);
}