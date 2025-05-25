package com.example.album.service;

import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private final String FLASK_URL = "http://photo-python:5000/";
    private static final Logger logger = LoggerFactory.getLogger(VideoService.class);
    private static final String Base_url = "http://localhost:8080";
    @Value("%{app.upload.dir}")
    private String audioDir;

    @Autowired
    private ServletContext servletContext;

    public Result<byte[]> CreateVideo(List<String> img_folder, MultipartFile audioFile, String transition, String fps) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = UriComponentsBuilder.fromUriString(FLASK_URL)
                    .path("/image_to_video")
                    .toUriString();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("img_folder", img_folder);
            requestBody.put("transition", transition);
            requestBody.put("fps", fps);

            // 将音频转为Base64传输
            if (audioFile != null && !audioFile.isEmpty()) {
                String audioBase64 = Base64.getEncoder().encodeToString(audioFile.getBytes());
                requestBody.put("audio_base64", audioBase64);
                requestBody.put("audio_filename", audioFile.getOriginalFilename());
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, byte[].class);

            byte[] videoData = response.getBody();
            if (videoData != null) {
                return Result.success(videoData);
            } else {
                throw new RuntimeException("no video data");
            }
        } catch (Exception e) {
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
        if (audio == null || audio.isEmpty()) {
            return null;
        }
        try {
            String realStaticPath = servletContext.getRealPath(audioDir + "/temp");
            File dir = new File(realStaticPath);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new IOException("创建目录失败: " + realStaticPath);
            }

            String filename = audio.getOriginalFilename();
            File dest = new File(dir, filename);
            audio.transferTo(dest);

            return Base_url + "/uploads/temp/" + filename;

        } catch (IOException e) {
            e.printStackTrace();
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
