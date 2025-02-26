package com.example.album.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum ResourceTypeEnum {
    ALBUM("album", "相册"),
    PHOTO("photo", "照片");

    @EnumValue
    private final String value;
    private final String desc;

    ResourceTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}