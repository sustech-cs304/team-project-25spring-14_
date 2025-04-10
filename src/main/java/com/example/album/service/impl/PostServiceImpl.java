package com.example.album.service.impl;

import com.example.album.vo.PhotoVO;
import org.springframework.web.multipart.MultipartFile;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.album.dto.PhotoStorageResult;
import com.example.album.dto.PostCreateDTO;
import com.example.album.dto.PostCreateWithPhotoDTO;
import com.example.album.dto.PostUpdateDTO;
import com.example.album.entity.Photo;
import com.example.album.entity.Post;
import com.example.album.entity.User;
import com.example.album.mapper.PhotoMapper;
import com.example.album.mapper.PostMapper;
import com.example.album.mapper.UserMapper;
//import com.example.album.service.CommentService;
//import com.example.album.service.LikeService;
import com.example.album.service.PostService;
import com.example.album.service.StorageService;
import com.example.album.vo.PostVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;
    private final PhotoMapper photoMapper;
    private final UserMapper userMapper;
//    private final LikeService likeService;
//    private final CommentService commentService;
    private final StorageService storageService;


    /**
     * AI-generated-content
     * tool: claude
     * version: latest
     * usage: give it the key of photo to ask it to generate the Post construct
     * copy
     */
    @Override
    @Transactional
    public PostVO createPost(PostCreateDTO createDTO, Integer userId) {
        // 验证照片存在且属于当前用户
        Photo photo = photoMapper.selectById(createDTO.getPhotoId());
        if (photo == null || !photo.getUserId().equals(userId)) {
            throw new RuntimeException("照片不存在或无权访问");
        }

        Post post = new Post();
        post.setUserId(userId);
        post.setCaption(createDTO.getCaption());
        post.setPrivacy(createDTO.getPrivacy());
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        postMapper.insert(post);
        log.info("用户 {} 创建了帖子 {}", userId, post.getPostId());

        return getPostById(post.getPostId(), userId);
    }

    /**
     * AI-generated-content
     * tool: claude
     * version: latest
     * usage: give it the key of photo to ask it to generate the Photo construct
     * copy and add the try catch part that in case it will failed
     */
    @Override
    @Transactional
    public PostVO createPostWithPhoto(PostCreateWithPhotoDTO createDTO, Integer userId) {
        // 用于跟踪已上传的文件，以便在失败时清理
        List<String> uploadedFileUrls = new ArrayList<>();

        try {
            Post post = new Post();
            post.setUserId(userId);
            post.setCaption(createDTO.getCaption());
            post.setPrivacy(createDTO.getPrivacy());
            post.setCreatedAt(LocalDateTime.now());
            post.setUpdatedAt(LocalDateTime.now());

            // 先插入帖子以获取postId
            postMapper.insert(post);
            log.info("用户 {} 创建了帖子 {}", userId, post.getPostId());

            List<MultipartFile> photos = createDTO.getPhoto();
            Photo firstPhoto = null;

            for (int i = 0; i < photos.size(); i++) {
                MultipartFile photoFile = photos.get(i);

                // 存储照片文件
                PhotoStorageResult storageResult = storageService.storePhoto(photoFile, userId);
                // 跟踪上传的文件URL，以便在失败时清理
                uploadedFileUrls.add(storageResult.getFileUrl());

                // 创建照片记录
                Photo photo = new Photo();
                photo.setUserId(0);
                photo.setAlbumId(0); // 社区专用相册ID为0
                photo.setFileName(storageResult.getOriginalFilename());
                photo.setFileUrl(storageResult.getFileUrl());
                photo.setThumbnailUrl(storageResult.getThumbnailUrl());
                photo.setPostId(post.getPostId()); // 设置关联到的帖子ID
                photo.setCapturedAt(storageResult.getCapturedAt() != null ?
                        storageResult.getCapturedAt() : LocalDateTime.now());
                photo.setCreatedAt(LocalDateTime.now());
                photo.setIsFavorite(false);
                photo.setLocation(storageResult.getLocation());
                photo.setTagName(storageResult.getTag());

                photoMapper.insert(photo);
                log.info("为社区帖子创建了照片记录，ID: {}", photo.getPhotoId());
            }

            return getPostById(post.getPostId(), userId);

        } catch (Exception e) {
            log.error("创建帖子失败", e);

            // 清理已上传的物理文件
            for (String fileUrl : uploadedFileUrls) {
                try {
                    log.info("正在清理上传的文件: {}", fileUrl);
                    storageService.deletePhoto(fileUrl);
                } catch (Exception cleanupEx) {
                    // 仅记录清理失败，不影响主异常的抛出
                    log.error("清理文件失败: {}, 错误: {}", fileUrl, cleanupEx.getMessage());
                }
            }

            throw new RuntimeException("创建帖子失败: " + e.getMessage());
        }
    }


    @Override
    @Transactional
    public PostVO updatePost(Integer postId, PostUpdateDTO updateDTO, Integer userId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }

        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("无权更新此帖子");
        }

        if (updateDTO.getCaption() != null) {
            post.setCaption(updateDTO.getCaption());
        }

        if (updateDTO.getPrivacy() != null) {
            post.setPrivacy(updateDTO.getPrivacy());
        }

        post.setUpdatedAt(LocalDateTime.now());
        postMapper.updatePost(post);
        log.info("用户 {} 更新了帖子 {}", userId, postId);

        return getPostById(postId, userId);
    }

    @Override
    @Transactional
    public void deletePost(Integer postId, Integer userId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }

        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此帖子");
        }
        List<Photo> photos = photoMapper.selectByPostId(postId);
        postMapper.deleteById(postId);
        for (Photo photo : photos) {
            if (photo.getAlbumId() != null && photo.getAlbumId() == 0) {
                try {
                    storageService.deletePhoto(photo.getFileUrl());
                    photoMapper.deleteById(photo.getPhotoId());
                    log.info("删除了社区专用照片 {}", photo.getPhotoId());
                } catch (Exception e) {
                    log.error("删除社区照片失败", e);
                }
            }
        }
        log.info("用户 {} 删除了帖子 {}", userId, postId);
    }

    @Override
    public PostVO getPostById(Integer postId, Integer currentUserId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }

        return convertToPostVO(post, currentUserId);
    }
    /**
     * AI-generated-content
     * tool: claude
     * version: latest
     * usage:ask for stream operation
     * copy
     */
    @Override
    public List<PostVO> getPostsByUserId(Integer userId, Integer currentUserId) {
        List<Post> posts = postMapper.selectByUserId(userId);
        return posts.stream()
                .map(post -> convertToPostVO(post, currentUserId))
                .collect(Collectors.toList());
    }

    /**
     * AI-generated-content
     * tool: claude
     * version: latest
     * usage:ask for stream operation
     * copy
     */
    @Override
    public List<PostVO> getPublicPosts(Integer currentUserId) {
        List<Post> posts = postMapper.selectPublicPosts();
        return posts.stream()
                .map(post -> convertToPostVO(post, currentUserId))
                .collect(Collectors.toList());
    }


    /**
     * AI-generated-content
     * tool: claude
     * version: latest
     * usage:ask for stream operation
     * copy
     */
    @Override
    public List<PostVO> getFollowingPosts(Integer userId) {
        List<Post> posts = postMapper.selectFollowingPosts(userId);
        return posts.stream()
                .map(post -> convertToPostVO(post, userId))
                .collect(Collectors.toList());
    }


    /**
     * AI-generated-content
     * tool: claude
     * version: latest
     * usage:the same as the ones in photo
     * copy and add some corner case its uncover
     */
    private PostVO convertToPostVO(Post post, Integer currentUserId) {
        if (post == null) {
            return null;
        }

        PostVO postVO = new PostVO();
        BeanUtils.copyProperties(post, postVO);
        if (post.getPrivacy() != null) {
            postVO.setPrivacy(post.getPrivacy().toString().toLowerCase());
        } else {
            postVO.setPrivacy("PUBLIC"); // 默认值
        }

        List<Photo> photos = photoMapper.selectByPostId(post.getPostId());
        List<PhotoVO> photoVOs = new ArrayList<>();

        for (Photo photo : photos) {
            PhotoVO photoVO = new PhotoVO();
            BeanUtils.copyProperties(photo, photoVO);

            // 确保URL是完整的
            String photoUrl = photo.getFileUrl();
            String thumbnailUrl = photo.getThumbnailUrl();

            if (!photoUrl.startsWith("http")) {
                photoUrl = storageService.getFullUrl(photoUrl);
            }

            if (!thumbnailUrl.startsWith("http")) {
                thumbnailUrl = storageService.getFullUrl(thumbnailUrl);
            }

            photoVO.setFileUrl(photoUrl);
            photoVO.setThumbnailUrl(thumbnailUrl);

            photoVOs.add(photoVO);
        }

        postVO.setPhotos(photoVOs);
        // 获取用户信息
        User user = userMapper.selectById(post.getUserId());
        if (user != null) {
            postVO.setUsername(user.getUsername());
            postVO.setUserAvatar(user.getAvatarUrl());
        }

        // 获取点赞和评论数
//        postVO.setLikeCount(likeService.getLikeCountByPostId(post.getPostId()));
//        postVO.setCommentCount(commentService.getCommentCountByPostId(post.getPostId()));
//
//        // 检查当前用户是否点赞
//        if (currentUserId != null) {
//            postVO.setIsLiked(likeService.isPostLikedByUser(post.getPostId(), currentUserId));
//        } else {
//            postVO.setIsLiked(false);
//        }

        return postVO;
    }
}