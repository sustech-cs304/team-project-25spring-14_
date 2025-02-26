package com.example.album.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum UserStatusEnum {
    ACTIVE("active", "正常"),
    DISABLED("disabled", "禁用");

    @EnumValue
    private final String value;
    private final String desc;

    UserStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}