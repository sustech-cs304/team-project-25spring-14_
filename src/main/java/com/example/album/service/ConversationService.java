package com.example.album.service;

import com.example.album.mapper.ConversationMapper;
import com.example.album.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.album.entity.Conversation;
import com.example.album.entity.Message;
import java.util.List;

@Service
public class ConversationService {

    private final ConversationMapper conversationMapper;
    private final MessageMapper messageMapper;

    @Autowired
    public ConversationService(ConversationMapper conversationMapper, MessageMapper messageMapper) {
        this.conversationMapper = conversationMapper;
        this.messageMapper = messageMapper;
    }

    // 创建或获取对话
    public Conversation createOrGetConversation(Integer userId1, Integer userId2) {
        Conversation conversation = conversationMapper.findConversation(userId1, userId2);
        if (conversation == null) {
            // 如果没有对话，创建一个新的对话
            conversationMapper.createConversation(userId1, userId2);
            return conversationMapper.findConversation(userId1, userId2);
        }
        return conversation;
    }

    // 发送消息
    public void sendMessage(Integer senderId, Integer recipientId, String content) {

        Conversation conversation = createOrGetConversation(senderId, recipientId);
        messageMapper.sendMessage(conversation.getConversationId(), senderId, content);
    }

    // 获取消息历史
    public List<Message> getMessages(Integer userId1, Integer userId2) {
        Conversation conversation = createOrGetConversation(userId1, userId2);
        return messageMapper.getMessagesByConversationId(conversation.getConversationId());
    }
}

