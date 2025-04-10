package com.example.album.service;

import org.springframework.stereotype.Service;

@Service
public interface LikeService {
    boolean likePost(Integer postId, Integer userId);

    boolean unlikePost(Integer postId, Integer userId);
}
