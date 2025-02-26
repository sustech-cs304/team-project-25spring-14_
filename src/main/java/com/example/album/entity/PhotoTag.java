package com.example.album.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@TableName("tb_photo_tag")
public class PhotoTag implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("photo_id")
    private Integer photoId;

    @TableField("tag_id")
    private Integer tagId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhotoTag photoTag = (PhotoTag) o;
        if (photoId == null || tagId == null) return false;
        return photoId.equals(photoTag.photoId) && tagId.equals(photoTag.tagId);
    }

    @Override
    public int hashCode() {
        return (photoId == null ? 0 : photoId.hashCode()) ^ (tagId == null ? 0 : tagId.hashCode());
    }
}