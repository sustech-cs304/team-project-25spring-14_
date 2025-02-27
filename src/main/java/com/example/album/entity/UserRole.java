package com.example.album.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@TableName("tb_user_role")
public class UserRole implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "ur_id", type = IdType.AUTO)
    private Integer urId;

    @TableField("user_id")
    private Integer userId;

    @TableField("role_id")
    private Integer roleId;

    // MyBatis-Plus 会使用 equals 和 hashCode 方法来比较对象
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRole userRole = (UserRole) o;
        if (userId == null || roleId == null) return false;
        return userId.equals(userRole.userId) && roleId.equals(userRole.roleId);
    }

    @Override
    public int hashCode() {
        return (userId == null ? 0 : userId.hashCode()) ^ (roleId == null ? 0 : roleId.hashCode());
    }
}