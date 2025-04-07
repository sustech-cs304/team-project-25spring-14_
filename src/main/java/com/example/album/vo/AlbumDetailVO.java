package com.example.album.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class AlbumDetailVO extends AlbumVO {

    private UserVO user;

    private Integer photoCount;

    private List<PhotoVO> latestPhotos;
}