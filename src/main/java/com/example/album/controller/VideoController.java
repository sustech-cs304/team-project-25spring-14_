package com.example.album.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.album.service.VideoService;
@RestController
@RequestMapping("/video")
@Slf4j
public class VideoController {
    private static VideoService videoService;
//    @GetMapping("/get")
//    public ResponseEntity<byte[]> GetVideo() {
//        return videoService.GetVideo();
//    }
}
