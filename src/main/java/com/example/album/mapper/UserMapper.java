package com.example.album.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.album.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    //search by name
    @Select("SELECT * FROM tb_user WHERE username = #{username}")
    User selectByUsername(@Param("username") String username);

    //search by email
    @Select("SELECT * FROM tb_user WHERE email = #{email}")
    User selectByEmail(@Param("email") String email);

    //search page by page
    @Select("SELECT * FROM tb_user WHERE status = 'active' ORDER BY created_at DESC")
    IPage<User> selectUserPage(Page<User> page);
}