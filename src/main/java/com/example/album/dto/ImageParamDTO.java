package com.example.album.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实例化的时候先全部设置成null<br/>
 * 在controller实例化的时候再用set赋值
 */
@Data
@NoArgsConstructor
public class ImageParamDTO {
    private String img_path;
    private String region;
    private String brightness;
    private String contrast;
    private String mask_region;

}