package com.example.album.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.album.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    //search by name
    @Select("SELECT * FROM tb_user WHERE username = #{username}")
    User findByUsername(String username);

    //add user
    @Insert("INSERT INTO tb_user (username, password, email, avatar_url, last_login) " +
            "VALUES (#{username}, #{password}, null, null, null)")
    void add(String username, String password);

    //update user
    @Update("UPDATE tb_user SET username = #{username}, email = #{email} WHERE user_id = #{userId}")
    void update(User user);

    //update avatar
    @Update("UPDATE tb_user SET avatar_url = #{avatarUrl} WHERE user_id = #{id}")
    void updateAvatar(String avatarUrl, Integer id);

    //update password
    @Update("UPDATE tb_user SET password = #{md5String} WHERE user_id = #{id}")
    void updatePwd(String md5String, Integer id);

    //search by email
    @Select("SELECT * FROM tb_user WHERE email = #{email}")
    User selectByEmail(@Param("email") String email);

    //search page by page
    @Select("SELECT * FROM tb_user WHERE status = 'active' ORDER BY created_at DESC")
    IPage<User> selectUserPage(Page<User> page);
}