package com.example.album.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.album.entity.Comment;
import com.example.album.service.CommentService;
import java.util.List;

@RestController
@RequestMapping("/community/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // 添加评论
    @PostMapping
    public Comment addComment(@RequestParam int postId,
                              @RequestParam int userId,
                              @RequestParam String content) {
        return commentService.addComment(postId, userId, content);
    }

    // 根据评论 ID 获取评论
    @GetMapping("/{commentId}")
    public Comment getCommentById(@PathVariable int commentId) {
        return commentService.getComment(commentId);
    }

    // 获取某个帖子的所有评论
    @GetMapping("/post/{postId}")
    public List<Comment> getCommentsByPostId(@PathVariable int postId) {
        return commentService.getCommentsByPostId(postId);
    }

    // 删除评论
    @DeleteMapping("/{commentId}")
    public boolean deleteComment(@PathVariable int commentId) {
        return commentService.deleteComment(commentId);
    }
}

