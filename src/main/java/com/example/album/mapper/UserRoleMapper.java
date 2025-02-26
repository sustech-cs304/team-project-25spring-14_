package com.example.album.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.album.entity.UserRole;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    // search
    @Select("SELECT user_id FROM tb_user_role WHERE role_id = #{roleId}")
    List<Integer> selectUserIdsByRoleId(@Param("roleId") Integer roleId);

    // delete
    @Delete("DELETE FROM tb_user_role WHERE user_id = #{userId} AND role_id = #{roleId}")
    int deleteByUserIdAndRoleId(@Param("userId") Integer userId, @Param("roleId") Integer roleId);

    // insert
    @Insert("INSERT INTO tb_user_role(user_id, role_id) VALUES(#{userId}, #{roleId})")
    int insertUserRole(@Param("userId") Integer userId, @Param("roleId") Integer roleId);
}