package com.example.album.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.album.entity.Album;
import com.example.album.entity.User;

public interface UserService  {
    User findByUsername(String username);

    void register(String username, String password);

    void update(User user);

    void updateAvatar(String avatarUrl);

    void updatePwd(String newPwd);
}
