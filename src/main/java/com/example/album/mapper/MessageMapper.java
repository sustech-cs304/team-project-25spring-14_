package com.example.album.mapper;
import org.apache.ibatis.annotations.*;
import com.example.album.entity.Message;
import java.util.List;

@Mapper
public interface MessageMapper {

    // 保存消息
    @Insert("INSERT INTO messages (conversation_id, sender_id, content, created_at) " +
            "VALUES (#{conversationId}, #{senderId}, #{content}, CURRENT_TIMESTAMP)")
    void sendMessage(@Param("conversationId") Integer conversationId,
                     @Param("senderId") Integer senderId,
                     @Param("content") String content);

    // 获取对话中的所有消息
    @Select("SELECT * FROM messages WHERE conversation_id = #{conversationId} ORDER BY created_at")
    List<Message> getMessagesByConversationId(@Param("conversationId") Integer conversationId);
}
