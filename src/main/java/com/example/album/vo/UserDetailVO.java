package com.example.album.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserDetailVO extends UserVO {

    private Integer albumCount;

    private Integer photoCount;

    private List<String> roles;
}