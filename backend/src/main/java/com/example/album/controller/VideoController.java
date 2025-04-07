package com.example.album.controller;

import com.example.album.entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.album.service.VideoService;
import com.example.album.dto.CaptionParamDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/video")
@Slf4j
public class VideoController {
    private static VideoService videoService;

    /**
     * 需要获取这个视频的地址，然后会将这个视频的二进制文件返回给前端
     * @param userId
     * @param videoId
     * @return
     */
    @GetMapping("/get")
    public Result GetVideo(@RequestParam int userId, @RequestParam int videoId) {
        String url = "temp";
        return videoService.GetVideo(url);
    }

    /**
     * 用户可以自己选择一些图片来生成视频，后面的数组是对应的photoid
     * 然后还需要上传一个音频文件的参数，需要决定是否存到本地（数据库）
     * @param userId
     * @param PhotoId
     * @return
     */
    @PostMapping("/create_photo")
    public Result CreateVideo(@RequestParam int userId, @RequestParam int[] PhotoId, @RequestParam String transition, @RequestParam String fps) {
        List<String> urls = new ArrayList<>(); // 这个需要用userid和photoid从表格里面取出所哟图片的url
        String audio = "temp";  // 上传的音频路径，python是直接从本地读取音频，可以尝试弄一个专门用来缓存的文件夹，用完再删掉
        String output = "temp";  //存到本地的路径
        return videoService.CreateVideo(urls,audio,output,transition,fps);
    }

    /**
     * 通过tag来找图片并且生成视频
     * @param userId
     * @param Tag
     * @param fps
     * @param transition
     * @return Result
     */
    @PostMapping("/create_video_tag")
    public Result Create_video_tag(@RequestParam int userId, @RequestParam String Tag, @RequestParam String fps, @RequestParam String transition) {
        List<String> urls = new ArrayList<>(); // 这是通过tag找到的所有图片的url
        String audio = "temp";
        String output = "temp";
        return videoService.CreateVideo(urls,audio,output,transition,fps);
    }

    /**
     * 这是添加弹幕的方法，需要已经生成的视频路径以及这些从前端传过来的参数
     * 前端可能会传一个userid或者其他参数，主要是看你video怎么数据库怎么设计和怎么存到本地
     * @param input_video
     * @param output_video
     * @param tag
     * @param font_name
     * @param font_size
     * @param font_color
     * @return
     */
    @PostMapping("/add_caption")
    public Result add_caption(
            @RequestParam String input_video,
            @RequestParam String output_video,
            @RequestParam Map<String,String> tag,
            @RequestParam String font_name,
            @RequestParam String font_size,
            @RequestParam String font_color
    ){
        CaptionParamDTO captionParamDTO = new CaptionParamDTO(input_video,output_video,tag,font_name,font_size,font_color);
        return videoService.add_captions(captionParamDTO);
    }
}
