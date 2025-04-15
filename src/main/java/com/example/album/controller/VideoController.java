package com.example.album.controller;

import com.example.album.entity.Photo;
import com.example.album.entity.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import com.example.album.service.VideoService;
import com.example.album.dto.CaptionParamDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.example.album.mapper.PhotoMapper;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/video")
@Slf4j
@RequiredArgsConstructor()
public class VideoController {
    private final VideoService videoService;
    private final PhotoMapper photoMapper;

    /**
     * 需要获取这个视频的地址，然后会将这个视频的二进制文件返回给前端
     * @param userId 用户id
     * @param PhotoId 图片对应的id
     * @return 返回一个二进制文件
     */
    @GetMapping("/get")
    public Result<byte[]> GetVideo(@RequestParam int userId, @RequestParam int PhotoId) {
        String url = "temp";
        return videoService.GetVideo(url);
    }

    /**
     * 用户可以自己选择一些图片来生成视频，后面的数组是对应的photoid
     * 然后还需要上传一个音频文件的参数，需要决定是否存到本地（数据库）
     * @param PhotoId 图片对应的id
     * @return 返回二进制文件
     */
    @PostMapping("/create_photo")
    public Result<byte[]> CreateVideo(
            @RequestParam int[] PhotoId,
            @RequestParam String transition,
            @RequestParam String fps,
            @RequestPart("audio") MultipartFile audioFile
            ) {
        List<String> urls = new ArrayList<>(); // 这个需要用userid和photoid从表格里面取出所哟图片的url
        for (int j : PhotoId) {
            Photo photo = photoMapper.selectById(j);
            urls.add(photo.getFileUrl());
        }
        String audio = videoService.storeAudio(audioFile);
        return videoService.CreateVideo(urls,audio,transition,fps);
    }

    /**
     * 通过tag来找图片并且生成视频
     * @param Tag 选择生成视频的标签
     * @param fps 帧率，默认25
     * @param transition 变换的方式
     * @return Result
     */
    @PostMapping("/create_video_tag")
    public Result<byte[]> Create_video_tag(
            @RequestParam String Tag,
            @RequestParam String fps,
            @RequestParam String transition,
            @RequestPart("audio") MultipartFile audioFile
            ) {
        List<String> urls = new ArrayList<>(); // 这是通过tag找到的所有图片的url
        List<Photo> photos = photoMapper.findPhotosByTag(Tag);
        for (Photo photo : photos) {
            urls.add(photo.getFileUrl());
        }
        String audio = videoService.storeAudio(audioFile);
        return videoService.CreateVideo(urls,audio,transition,fps);
    }

    @PostMapping("/add_caption")
    public Result<byte[]> add_caption(
            @RequestParam String photoid,
            @RequestParam Map<String,String> tag,
            @RequestParam String font_name,
            @RequestParam String font_size,
            @RequestParam String font_color
    ){
        Photo photo = photoMapper.selectById(photoid);
        String url = photo.getFileUrl();
        CaptionParamDTO captionParamDTO = new CaptionParamDTO(url,tag,font_name,font_size,font_color);
        return videoService.add_captions(captionParamDTO);
    }
}
