package com.example.album.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@TableName("messages")
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private Integer messageId;
    private Integer conversationId;
    private Integer senderId;
    private String content;
    private LocalDateTime createdAt;
}

