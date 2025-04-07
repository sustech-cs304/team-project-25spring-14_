package com.example.album.controller;


import com.example.album.entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.album.service.ConversationService;
import com.example.album.entity.Message;

import java.util.List;

@RestController
@RequestMapping("message")
@Slf4j
@Validated
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    // 发送消息
    @PostMapping("/send")
    public Result sendMessage(@RequestParam Integer senderId,
                              @RequestParam Integer recipientId,
                              @RequestParam String content) {
        try {
            log.info("发送信息");
            conversationService.sendMessage(senderId, recipientId, content);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // 获取消息历史
    @GetMapping("/history")
    public Result<List<Message>> getConversationHistory(@RequestParam Integer userId1,
                                                        @RequestParam Integer userId2) {
        try {
            List<Message> messages = conversationService.getMessages(userId1, userId2);
            return Result.success(messages);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

