package com.example.album.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_comment")
public class Comment {
    private Integer commentId;
    private Integer userId;
    private Integer postId;
    private String content;
    private LocalDateTime created_at;


}
