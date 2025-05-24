package com.example.album.service;

import com.example.album.mapper.PhotoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.example.album.entity.Photo;
import com.example.album.dto.ImageParamDTO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 这里主要是图片跟python交互，包括传输图片和获取图片，关于视频的类会单独再写一个class来实现 <br/>
 * 里面都需要传进去savePath这个参数，如果非空就进行存储<br/>
 * 这个的返回值也是一个完整的http响应，内容就是flask传过来的二进制文件<br/>
 * 注意的是remove_object还没有完全实现
 */
@Service
public class ImageService {  // 这个是图片的一下基础操作，现在不需要用了

    private static final String FLASK_URL = "http://photo-python:5000/";
    private static final String SHARED_STORAGE_PATH = "/app/storage";
    @Autowired
    private PhotoMapper photoMapper;

    public ResponseEntity<byte[]> GetAndSave(ImageParamDTO imageParam, String savePath, String option) {
        UriComponentsBuilder url = UriComponentsBuilder.fromUriString(FLASK_URL)
                .path(option)
                .queryParam("img_path",imageParam.getImg_path());

        if (imageParam.getRegion() != null) {
            url.queryParam("region", imageParam.getRegion());
        }
        if (imageParam.getBrightness() != null) {
            url.queryParam("brightness", imageParam.getBrightness());
        }
        if (imageParam.getContrast() != null) {
            url.queryParam("contrast", imageParam.getContrast());
        }
        if (imageParam.getMask_region() != null) {
            url.queryParam("mask_region", imageParam.getMask_region());
        }
        String Url = url.build().toUriString();

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());

        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    Url,
                    HttpMethod.GET,
                    null,
                    byte[].class
            );
            // 你们来确定存到哪个地方，以及数据库怎么进行修改，这里是直接用RGB的格式存到本地里面去
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null && !savePath.isEmpty()) {
                Path outputPath = Paths.get(savePath);
                try {
                    Files.write(outputPath, response.getBody());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_JPEG); // 说明content-type是img/jpg文件
                return new ResponseEntity<>(response.getBody(), headers, HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing image".getBytes());
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public String ai_classify(String url){  // 这个ai返回值是一个字符串，可以直接对应到数据库里面去
        String containerFilePath = convertUrlToContainerPath(url);
        // String Url = UriComponentsBuilder.fromUriString(FLASK_URL)
        //         .path("ai_classify_image")
        //         .queryParam("img_path",url)
        //         .build()
        //         .toUriString();

        log.info("🔍 原始URL: {}", url);
        log.info("🔍 容器内路径: {}", containerFilePath);
        String requestUrl = UriComponentsBuilder.fromUriString(FLASK_URL)
                .path("ai_classify_image")
                .queryParam("img_path", containerFilePath)  // 传递容器内路径
                .build()
                .toUriString();
        
        RestTemplate restTemplate = new RestTemplate();
        log.info("🔍 请求Python服务: {}", requestUrl);
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    requestUrl,
                    HttpMethod.GET,
                    null,
                    Map.class
            );
            if (response.getStatusCode() == HttpStatus.OK || response.getBody()!=null) {
                Object detectClassObj = Objects.requireNonNull(response.getBody()).get("detect_class");
                if (detectClassObj instanceof String) {
                    return detectClassObj.toString();  // 返回 detect_class 列表
                } else {
                    return null;  // 如果类型不匹配，则返回 null
                }
            }

            else{
                return null;
            }
        }
        catch (ResourceAccessException e) {
            // 捕获连接相关异常（如连接被拒绝、无法连接到服务器等）
            return null;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将HTTP URL转换为容器内文件路径
     * http://localhost:8080/uploads/storage/1/xxx.jpg -> /app/storage/1/xxx.jpg
     */
    private String convertUrlToContainerPath(String fileUrl) {
        if (fileUrl.startsWith("http://localhost:8080/uploads")) {
            // 提取相对路径部分
            String relativePath = fileUrl.replace("http://localhost:8080/uploads", "");
            return SHARED_STORAGE_PATH + relativePath;
        }
        return fileUrl; // 如果已经是文件路径，直接返回
    }
}
