package com.example.album;

import org.junit.jupiter.api.Test;
import com.example.album.dto.ImageParamDTO;
import com.example.album.dto.CaptionParamDTO;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.album.service.ImageService;
import com.example.album.service.VideoService;
import com.example.album.service.VideoService;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImageServiceTest {

//    @Test
//    public void test() {
//        String img_path = "F:/VOCtrainval_11-May-2012/JPEGImages/2007_000027.jpg";
//        String save_path = "F:\\VOCtrainval_11-May-2012\\output.jpg";
////        String region = "((100,200),(100,200))";
//        String brightness = "100";
//        String contrast = "1";
//        ImageParamDTO param = new ImageParamDTO();
//        param.setImg_path(img_path);
//        param.setBrightness(brightness);
//        param.setContrast(contrast);
////        param.setRegion(region);
//        ImageService imageService = new ImageService();
//        ResponseEntity<byte[]> result = imageService.GetAndSave(param, save_path, "adjust_brightness");
//        Path outputPath = Paths.get(save_path);
//        assertTrue(Files.exists(outputPath));
    }

//    @Test
//    public void test2() {
//        ImageParam param = new ImageParam();
//        ImageService imageService = new ImageService();
//        String img_path = "F:/VOCtrainval_11-May-2012/JPEGImages/2007_000323.jpg";
//        param.setImg_path(img_path);
//        List<String> result = imageService.ai_classify(img_path);
//        List<String> temp = new ArrayList<>();
//        temp.add("person");
//        assertTrue(temp.equals(result));
//    }

//    @Test
//    public void test3() {
//        String img_folder = "F:/VOCtrainval_11-May-2012/JPEGImages";
//        String audio_file = "E:/bgMusic.wav";
//        String transition = "fade";
//        String fps = "25";
//        String final_output_file = "F:/VOCtrainval_11-May-2012/FinalOutput.mp4";
//        VideoService videoService = new VideoService();
//        videoService.CreateVideo(img_folder,audio_file,final_output_file,transition,fps);
//        Path outputPath = Paths.get(final_output_file);
//        assertTrue(Files.exists(outputPath));
//    }
//
//    @Test
//    public void test4() {
//        Map<String, String> subtitles = new HashMap<>();
//        subtitles.put("00:00:05-00:00:10", "第一段字幕：欢迎观看！");
//        subtitles.put("00:00:15-00:00:20", "第二段字幕：这是一个示例视频。");
//        VideoService videoService = new VideoService();
//        String input_video = "F:/VOCtrainval_11-May-2012/FinalOutput.mp4";
//        String output_video = "F:/VOCtrainval_11-May-2012/FinalOutput_captions.mp4";
//        String font_name = "Arial";
//        String font_size = "18";
//        String font_color = "&H00FFFFFF";
//        CaptionParamDTO captionParamDTO = new CaptionParamDTO(input_video,output_video,subtitles,font_name,font_size,font_color);
//        videoService.add_captions(captionParamDTO);
//        Path outputPath = Paths.get(output_video);
//        assertTrue(Files.exists(outputPath));
//    }
//}
