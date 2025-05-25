package com.example.album.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptionParamDTO {
    private String input_video;
    private String subtitles_dict;
    private String font_name;
    private String font_size;
    private String font_color;

}