package com.example.album.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.album.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    //get the role of a user
    @Select("SELECT r.* FROM tb_role r " +
            "JOIN tb_user_role ur ON r.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<Role> selectRolesByUserId(@Param("userId") Integer userId);
}