package com.example.album.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.album.entity.Album;
import com.example.album.entity.User;
import com.example.album.vo.UserVO;

import java.util.List;

public interface UserService  {
    User findByUsername(String username);

    void register(String username, String password);

    void update(User user);

    void updateAvatar(String avatarUrl);

    void updatePwd(String newPwd);

    List<UserVO> getAllUsers();

    User getUserById(long userId);
}
