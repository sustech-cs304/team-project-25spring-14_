package com.example.album.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_comment")
public class Comment {
    private Integer comment_id;
    private Integer user_id;
    private Integer post_id;
    private String content;
    private LocalDateTime create_time;
}
