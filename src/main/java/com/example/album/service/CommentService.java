package com.example.album.service;

import com.example.album.mapper.CommentMapper;
import com.example.album.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {
    private final CommentMapper commentMapper;

    @Autowired
    public CommentService(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }
    // 用这个添加一条评论
    @Transactional
    public Comment addComment(int postId,int userId,String content) {
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(content);
        commentMapper.insertComment(comment);
        return comment;
    }
    // 用id来获取评论
    public Comment getComment(int commentId) {
        return commentMapper.selectCommentById(commentId);
    }
    // 找出一个post里面所有的评论
    public List<Comment> getCommentsByPostId(int postId) {
        return commentMapper.selectCommentsByPostId(postId);
    }
    // 删掉某一个评论
    @Transactional
    public boolean deleteComment(int commentId) {
        int Affected =commentMapper.deleteComment(commentId);
        return Affected>0;
    }
}
