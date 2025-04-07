package com.example.album.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum PrivacyTypeEnum {
    PRIVATE("private", "私有"),
    PUBLIC("public", "公开"),
    SHARED("shared", "共享");

    @EnumValue
    private final String value;
    private final String desc;

    PrivacyTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}