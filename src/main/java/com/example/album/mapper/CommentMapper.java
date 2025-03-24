package com.example.album.mapper;

import org.apache.ibatis.annotations.*;
import com.example.album.entity.Comment;
import java.util.List;

public interface CommentMapper {


    @Insert("INSERT INTO tb_comment (post_id, user_id, content) " +
            "VALUES (#{postId}, #{userId}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "commentId")
    int insertComment(Comment comment);


    @Select("SELECT * FROM tb_comment WHERE comment_id = #{commentId}")
    Comment selectCommentById(@Param("commentId") int commentId);


    @Select("SELECT * FROM tb_comment WHERE post_id = #{postId}")
    List<Comment> selectCommentsByPostId(@Param("postId") int postId);


    @Delete("DELETE FROM tb_comment WHERE comment_id = #{commentId}")
    int deleteComment(@Param("commentId") int commentId);

}

