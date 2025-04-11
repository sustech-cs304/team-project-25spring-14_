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
        return true;
    }

    @Override
    public boolean unlikePost(Integer postId, Integer userId) {
        likeMapper.unLikePost(postId, userId);
        return true;
    }
}
