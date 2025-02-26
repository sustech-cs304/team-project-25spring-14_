package com.example.album.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tb_ai_task")
public class AiTask {

    @TableId(type = IdType.AUTO)
    private Integer taskId;

    private Integer photoId;

    private String taskType;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime completedAt;
}