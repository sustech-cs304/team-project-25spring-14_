package com.example.album.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@TableName("conversations")
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {
    private Integer conversationId;
    private Integer userId1;
    private Integer userId2;
    private LocalDateTime createdAt;
}

