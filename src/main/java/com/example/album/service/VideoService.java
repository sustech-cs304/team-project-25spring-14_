package com.example.album.service;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.album.dto.CaptionParamDTO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.album.entity.Result;

@Service
@Component
public class VideoService {
    private final String FLASK_URL = "http://localhost:5000/";
    private static final Logger logger = LoggerFactory.getLogger(VideoService.class);

    public Result CreateVideo(List<String> img_folder, String audio_file, String final_output_file, String transition, String fps) {  //这个方法会直接将生成的视频保存到本地，如果需要
        RestTemplate restTemplate = new RestTemplate();

        String url = UriComponentsBuilder.fromUriString(FLASK_URL)
                .path("/image_to_video")
                .toUriString();

        Map<String, Object> requestBody = new HashMap<>();  // 需要给前端发送一个json格式的参数
        requestBody.put("img_folder", img_folder);
        requestBody.put("audio_file", audio_file);
        requestBody.put("final_output_file", final_output_file);
        requestBody.put("transition", transition);
        requestBody.put("fps", fps);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
            logger.info(response.getBody(), response.getStatusCode());  // 用日志显示出body和状态码
            return Result.success();
        } catch (Exception e) {
            // 处理请求失败的情况
            logger.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    public Result add_captions(CaptionParamDTO caption) {
        RestTemplate restTemplate = new RestTemplate();
        String url = UriComponentsBuilder.fromUriString(FLASK_URL)
                .path("add_captions")
                .toUriString();
        Map<String,Object> requestBody = new HashMap<>();
        requestBody.put("input_video",caption.getInput_video());
        requestBody.put("output_video",caption.getOutput_video());
        requestBody.put("subtitles_dict",caption.getSubtitles_dict());
        requestBody.put("font_name",caption.getFont_name());
        requestBody.put("font_size",caption.getFont_size());
        requestBody.put("font_color",caption.getFont_color());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        try{
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
            logger.info(response.getBody(), response.getStatusCode());
            return Result.success();
        } catch (Exception e){
            logger.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    public Result GetVideo(String video_path) {  // 读取文件，返回一个封装好了视频二进制编码的ResponseEntity
        try {
            File videoFile = new File(video_path);
            if (!videoFile.exists()) {
                return Result.error("视频路径不存在");
            }
            byte[] videoBytes = Files.readAllBytes(videoFile.toPath());
            return Result.success(videoBytes);
        } catch (IOException e) {
            return Result.error(e.getMessage());
        }
    }

    public Result DeleteVideo(String video_path) {
        try {
            Path path = Paths.get(video_path);
            Files.delete(path);
            logger.info("delete video file from {}", video_path);
            return Result.success();
        } catch (IOException e) {
            logger.error("Error occurred while deleting the video file", e);
            return Result.error(e.getMessage());
        }
    }
}
