package com.example.album.service;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.album.dto.CaptionParamDTO;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import com.example.album.entity.Result;

@Service
@Component
public class VideoService {
    private final String FLASK_URL = "http://localhost:5000/";
    private static final Logger logger = LoggerFactory.getLogger(VideoService.class);


    public Result<byte[]> CreateVideo(List<String> img_folder, String audio_file, String transition, String fps) {  //这个方法会直接将生成的视频保存到本地，如果需要
        try {
            RestTemplate restTemplate = new RestTemplate();

            String url = UriComponentsBuilder.fromUriString(FLASK_URL)
                    .path("/image_to_video")
                    .toUriString();

            Map<String, Object> requestBody = new HashMap<>();  // 需要给前端发送一个json格式的参数
            requestBody.put("img_folder", img_folder);
            requestBody.put("audio_file", audio_file);
//        requestBody.put("final_output_file", final_output_file);
            requestBody.put("transition", transition);
            requestBody.put("fps", fps);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    byte[].class
            );
            byte[] videoData = response.getBody();
//            if (videoData != null) {
//                // 将二进制数据保存为本地的 mp4 文件
//                try (FileOutputStream fos = new FileOutputStream(final_output_file)) {
//                    fos.write(videoData);
//                    logger.info("Video saved successfully to: " + final_output_file);
//                } catch (IOException e) {
//                    logger.error("Error saving video file: " + e.getMessage(), e);
//                    return Result.error("Error saving video file: " + e.getMessage());
//                }
//            }
//            logger.info(response.getBody(), response.getStatusCode());  // 用日志显示出body和状态码
            if (videoData != null){
                return Result.success(videoData);
            } else {
                throw new RuntimeException("no video data");
            }
        } catch (Exception e) {
            // 处理请求失败的情况
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public Result<byte[]> add_captions(CaptionParamDTO caption) {
        RestTemplate restTemplate = new RestTemplate();
        String url = UriComponentsBuilder.fromUriString(FLASK_URL)
                .path("add_captions")
                .toUriString();
        Map<String,Object> requestBody = new HashMap<>();
        requestBody.put("input_video",caption.getInput_video());
        requestBody.put("subtitles_dict",caption.getSubtitles_dict());
        requestBody.put("font_name",caption.getFont_name());
        requestBody.put("font_size",caption.getFont_size());
        requestBody.put("font_color",caption.getFont_color());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        try{
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    byte[].class
            );
            byte[] video_data = response.getBody();
            if (video_data != null){
                return Result.success(video_data);
            } else {
                throw new RuntimeException("no video data");
            }
        } catch (Exception e){
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public Result<byte[]> GetVideo(String video_path) {  // 读取文件，返回一个封装好了视频二进制编码的ResponseEntity
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

    public String storeAudio(MultipartFile audio) {
        try{
            String upload = "./temp";
            String fileName = audio.getOriginalFilename();

            File directory = new File(upload);
            if (!directory.exists()) {
                boolean mkdirs = directory.mkdirs();
                if (!mkdirs) {
                    logger.error("mkdirs failed");
                    return null;
                }
            }
            File file = new File(upload + File.separator + fileName);
            audio.transferTo(file);  // 将音频文件保存到该路径
            return file.getAbsolutePath();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public boolean deleteTempo(String video_path) {
        File file = new File(video_path);
        return file.delete();
    }

    /**
     * 这是测试类使用的方法
     */
    public List<String> getPath(String image_folder) {
        try{
            return Files.walk(Paths.get(image_folder))
                    .filter(Files::isRegularFile)
                    .filter(file -> file.toString().endsWith(".jpg") || file.toString().endsWith(".png"))
                    .limit(100)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e){
            logger.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * 讲二进制文件转换成Multipartfile
     * @param video
     * @return
     */
    public MultipartFile convert(byte[] video){
        FileItemFactory factory = new DiskFileItemFactory();
        String fileName = UUID.randomUUID().toString() + ".mp4";  //随机生成一个名字，避免重复
        FileItem item = factory.createItem("file", "video/mp4", true, fileName);
        try (OutputStream os = item.getOutputStream()){
            os.write(video);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return (MultipartFile) item;
    }
}
