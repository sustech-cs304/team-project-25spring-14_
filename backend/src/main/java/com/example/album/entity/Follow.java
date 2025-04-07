package com.example.album.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@TableName("tb_follow")
@NoArgsConstructor
@AllArgsConstructor
public class Follow {

    @TableId(type = IdType.AUTO)
    private Integer followId;

    private Integer followerId;  // 关注者ID

    private Integer followingId;  // 被关注者ID

    private LocalDateTime createdAt;  // 关注时间
}