package com.example.album.service.impl;

import com.example.album.mapper.LikeMapper;
import com.example.album.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {
    @Autowired
    private LikeMapper likeMapper;

    @Override
    public boolean likePost(Integer postId, Integer userId) {
        likeMapper.likePost(postId, userId);
        likeMapper.addLikeCount(postId);
        return true;
    }

    @Override
    public boolean unlikePost(Integer postId, Integer userId) {
        likeMapper.unLikePost(postId, userId);
        likeMapper.deleteLikeCount(postId);
        return true;
    }

    @Override
    public boolean isLiked(Integer postId, Integer userId) {
        return likeMapper.isLiked(postId, userId);
    }
}
