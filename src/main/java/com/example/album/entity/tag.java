package com.example.album.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_tag")
public class Tag {

    @TableId(type = IdType.AUTO)
    private Integer tagId;

    private String name;

    private String tagType;
}
