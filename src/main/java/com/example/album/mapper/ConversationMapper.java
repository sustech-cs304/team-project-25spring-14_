package com.example.album.mapper;


import org.apache.ibatis.annotations.*;
import com.example.album.entity.Conversation;

@Mapper
public interface ConversationMapper {

    // 创建新对话
    @Insert("INSERT INTO conversations (user_id1, user_id2, created_at) " +
            "VALUES (#{userId1}, #{userId2}, CURRENT_TIMESTAMP)")
    void createConversation(@Param("userId1") Integer userId1, @Param("userId2") Integer userId2);

    @Select("SELECT * FROM conversations WHERE (user_id1 = #{userId1} AND user_id2 = #{userId2}) " +
            "OR (user_id1 = #{userId2} AND user_id2 = #{userId1})")
    Conversation findConversation(@Param("userId1") Integer userId1, @Param("userId2") Integer userId2);
}

