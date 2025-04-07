package com.example.album.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PhotoDetailVO extends PhotoVO {

    private AlbumVO album;

    private UserVO user;

//    private PhotoAiVO aiResult;
}