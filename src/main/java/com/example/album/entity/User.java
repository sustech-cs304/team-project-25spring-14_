package com.example.album.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.album.common.enums.UserStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@TableName("tb_user")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @TableId(type = IdType.AUTO)
    @NotNull
    private Integer userId;

    @NotEmpty
    @Pattern(regexp = "^\\S{5,16}$")
    private String rolename;
    @NotEmpty
    @Pattern(regexp = "^\\S{5,16}$")
    private String username;
    @JsonIgnore // 返回json时忽略该字段
    private String password;
    @NotEmpty
    @Email
    private String email;
    private String avatarUrl;
    private String status;
    private Long storageUsed;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
}