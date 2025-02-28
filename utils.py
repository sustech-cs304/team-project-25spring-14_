import cv2 as cv
import matplotlib.pyplot as plt
import numpy as np
import os
import shutil
from ultralytics import YOLO
from tqdm import tqdm
from moviepy import ImageSequenceClip, AudioFileClip, concatenate_audioclips
from PIL import Image
import subprocess
from datetime import datetime, timedelta

def rotate(image_path,save): # 每点击一下就逆时针旋转90度，然后判断是否需要保存
    img = cv.imread(image_path)
    img = cv.cvtColor(img, cv.COLOR_BGR2RGB)
    weight = img.shape[0]
    height = img.shape[1]
    ro_matrix = cv.getRotationMatrix2D((weight/2, height/2), 90, 1)
    img = cv.warpAffine(img, ro_matrix, (height, weight))
    if save:
        cv.imwrite(image_path,cv.cvtColor(img,cv.COLOR_RGB2BGR))
    return img
    # plt.imshow(img)
    # plt.axis('off')
    # plt.show()

def cut(img_path,region,save):  # 对图片进行裁剪
    img = cv.imread(img_path)
    print(img.shape)    
    img = cv.cvtColor(img, cv.COLOR_BGR2RGB)
    (x1,y1),(x2,y2) = region
    cut_img = img[x1:y1, x2:y2]
    if save:
        cv.imwrite(img_path,cv.cvtColor(img,cv.COLOR_RGB2BGR))
    # plt.imshow(cut_img)
    # plt.axis('off')
    # plt.show()

def adjust_brightness(img_path,save,brightness=0,contrast=1.0):  # 调整亮度，和对比度
    img = cv.imread('./resources/lenna.jpg')
    img = cv.cvtColor(img, cv.COLOR_BGR2RGB)
    plt.figure(figsize=(8,3))
    plt.subplot(121)
    plt.imshow(img)
    plt.subplot(122)
    img = cv.convertScaleAbs(img, alpha=contrast, beta=brightness)
    if save:
        cv.imwrite(img_path,cv.cvtColor(img,cv.COLOR_RGB2BGR))
    # plt.imshow(img)
    # plt.axis('off')
    # plt.show()

def remove_object(img_path,mask_region,save):  # 移除物体，但是移除之后
    img = cv.imread(img_path)
    img = cv.cvtColor(img, cv.COLOR_BGR2RGB)
    mask = np.zeros(img.shape[:2], dtype="uint8")
    (x1, y1), (x2, y2) = mask_region
    mask[x1:y2, x2:y2] = 255
    img_inpainted = cv.inpaint(img, mask, inpaintRadius=3, flags=cv.INPAINT_TELEA)
    if save:
        cv.imwrite(img_path,cv.cvtColor(img_inpainted,cv.COLOR_RGB2BGR))
    # plt.imshow(img_inpainted)
    # plt.axis('off')
    # plt.show()

def sketch_effect(img_path,save):  # 边缘检测，然后颜色反转得到所谓的素描图片
    img = cv.imread(img_path)
    img = cv.cvtColor(img, cv.COLOR_BGR2GRAY)

    edges = cv.Canny(img, 100, 200)
    inverted_edges = cv.bitwise_not(edges)
    if save:
        cv.imwrite(img_path,cv.cvtColor(inverted_edges,cv.COLOR_GRAY2BGR))    
    # plt.imshow(inverted_edges)
    # plt.axis('off')
    # plt.show()


def detect_people_in_photos(input_dir, output_dir, confidence_threshold=0.5):  # 还是将分类检测的结果预先存储到数据库里面，每加进来一张就进行一次判断

    model = YOLO('yolov8n.pt')
    os.makedirs(output_dir, exist_ok=True)

    # 获取所有图片文件
    image_extensions = ['.jpg', '.jpeg', '.png', '.webp']
    image_paths = [
        os.path.join(input_dir, f) for f in os.listdir(input_dir)
        if os.path.splitext(f)[1].lower() in image_extensions
    ]
    image_paths = image_paths[:100]  # 现在只选前一百张进行实验
    for image_path in tqdm(image_paths, desc="Processing Photos"):
        results = model.predict(image_path, verbose=False)
        for box in results[0].boxes:
            if box.cls == 0 and box.conf >= confidence_threshold:
                # 复制包含人物的图片到输出目录
                shutil.copy(image_path, output_dir)
                break  

def img_to_video_moviepy(image_folder, audio_path, output_video, fps=1, size=(640, 480)):  # 这个是将多张照片整合成一个视频，可以添加音频
    imgs = [os.path.join(image_folder, f) for f in os.listdir(image_folder) if f.endswith(('jpg', 'jpeg', 'png'))]
    imgs = imgs[:100]
    resized_imgs = []
    for img_path in imgs:
        # 用PIL打开图片并调整大小,因为这个IS这个方法要传进去一个numpy数组，而且还要统一大小
        # 640，480有些图片会拉伸，到时候再调整
        img = Image.open(img_path)
        img = img.resize(size)
        img = img.convert('RGB')
        resized_imgs.append(np.array(img))

    clip = ImageSequenceClip(resized_imgs, fps=fps)  # 报了一个没有shape属性的错
    audio = AudioFileClip(audio_path)

    video_duration = clip.duration
    audio_duration = audio.duration

    if audio.duration > clip.duration:
        audio = audio.subclipped(0,clip.duration)
    else:
        num_loops = int(video_duration // audio_duration) + 1
        audio_clips = [audio] * num_loops
        audio = concatenate_audioclips(audio_clips)
        audio = audio.subclipped(0, video_duration)  # 截取到视频长度

    clip.audio = audio  # 加背景音乐
    clip.write_videofile(output_video, codec='libx264',fps=fps)
    audio.close()
    clip.close()

def img_to_video(image_folder, audio_file, final_output_file, transition='', fps=25):

    fourcc = cv.VideoWriter_fourcc(*'mp4v')
    frame_size = (640, 480)  # 要注意resize之后是（480,640），因为传进去的是（width，height）
    temp_output = 'temp.mp4'
    video_writer = cv.VideoWriter(temp_output, fourcc, fps, frame_size)

    image_files = sorted([f for f in os.listdir(image_folder) if f.endswith(('.png', '.jpg'))])
    image_files = image_files[:100]

    if transition == '':  # 没有特效的
        for i in range(len(image_files) - 1):
            img_path = os.path.join(image_folder, image_files[i])
            img = cv.imread(img_path)
            img = cv.resize(img, frame_size)
            for _ in range(fps):
                video_writer.write(img)
    elif transition == 'fade':  # 渐变特效
        for i in range(len(image_files) - 1):
            img_path1 = os.path.join(image_folder, image_files[i])
            img_path2 = os.path.join(image_folder, image_files[i + 1])
            
            img1 = cv.imread(img_path1)
            img2 = cv.imread(img_path2)
            if img2 is None:  # 已经到了最后一张单独加上去
                continue

            img1 = cv.resize(img1, frame_size)
            img2 = cv.resize(img2, frame_size)

            for _ in range(fps):  # 静态帧，一秒25张
                video_writer.write(img1)
        
            num_transitions = fps  # 1秒的过渡帧数
            for j in range(num_transitions):
                alpha = j / float(num_transitions)
                blended = cv.addWeighted(img1, 1 - alpha, img2, alpha, 0)
                video_writer.write(blended)
        for _ in range(fps):  
            video_writer.write(img2)
    elif transition == 'slide':  # 滑动特效
        for i in range(len(image_files) - 1):
            img_path1 = os.path.join(image_folder, image_files[i])
            img_path2 = os.path.join(image_folder, image_files[i + 1])
            
            img1 = cv.imread(img_path1)
            img2 = cv.imread(img_path2)
            img1 = cv.resize(img1, frame_size)
            img2 = cv.resize(img2, frame_size)
            if img2 is None:
                continue
            for i in range(fps // 2):  # 每张照片先显示半秒然后开始过渡
                video_writer.write(img1)
            
            for i in range(fps):  # 过渡持续一秒，是从左边向又进行滑动
                alpha = i / float(fps)
                x_offset = int(alpha * frame_size[0])
                img = img1.copy()
                img[:, frame_size[0]-x_offset:] = img2[:, :x_offset]
                video_writer.write(img)
        for i in range(fps // 2):
            video_writer.write(img2)
    elif transition == 'zoom':
        col_middle = frame_size[0]//2
        row_middle = frame_size[1]//2
        for i in range(len(image_files) - 1):
            img_path1 = os.path.join(image_folder, image_files[i])
            img_path2 = os.path.join(image_folder, image_files[i + 1])
            
            img1 = cv.imread(img_path1)
            img2 = cv.imread(img_path2)
            img1 = cv.resize(img1, frame_size)
            img2 = cv.resize(img2, frame_size)
            if img2 is None:
                continue
            for i in range(fps // 2):
                video_writer.write(img1)

            for i in range(fps):
                alpha = i / float(fps)
                col_offset = int(alpha * frame_size[0])//2
                row_offset = int(alpha * frame_size[1])//2
                img = img1.copy()
                img[row_middle-row_offset:row_middle+row_offset, col_middle-col_offset:col_middle+col_offset] = img2[row_middle-row_offset:row_middle+row_offset, col_middle-col_offset:col_middle+col_offset]
                video_writer.write(img)
        for i in range(fps // 2):
            video_writer.write(img2)

    video_writer.release()  # 释放资源，不然最后会报警告
    print("Video saved successfully.")

    ffmpeg_cmd = [  # 用ffmpeg添加音频，但是这里得先生成视频再加音频
        "ffmpeg",
        "-i", temp_output,
        "-i", audio_file,
        "-c:v", "copy",
        "-c:a", "aac",
        "-shortest",
        final_output_file
    ]

    subprocess.run(ffmpeg_cmd)
    os.remove(temp_output)
    print("Final video with audio created.")

def add_captions(input_video, output_video, subtitles_dict:dict,font_name='Arial',font_size=24,font_color='white',position='bottom'):
    """
    这里用ffmpeg来添加字幕，因为使用cv的话需要逐帧渲染太麻烦了，而且不易于前端的操作
    这里面的字典，key是输入的时间，value是字幕内容
    将字典转换成.srt字幕文件，将字幕硬编码到视频
    """
    # video = cv.VideoCapture(input_video)
    # fps = int(video.get(cv.CAP_PROP_FPS))
    # width = int(video.get(cv.CAP_PROP_FRAME_WIDTH))
    # height = int(video.get(cv.CAP_PROP_FRAME_HEIGHT))

    # fourcc = cv.VideoWriter_fourcc(*'mp4v')
    # writer = cv.VideoWriter(output_video,fourcc,fps,(width,height))
    srt_content = []
    for idx,(time_range,text) in enumerate(subtitles_dict.items(),1):
        start_time, end_time = time_range.split("-")
        start_time =datetime.strptime(start_time, "%H:%M:%S")
        end_time =datetime.strptime(end_time, "%H:%M:%S")
        # 这里转换成ffmpeg时间格式
        start_srt = start_time.strftime("%H:%M:%S,000")
        end_srt = (end_time - timedelta(seconds=1)).strftime("%H:%M:%S,999")  # 避免时间重叠
        srt_content.append(
            f"{idx}\n"
            f"{start_srt} --> {end_srt}\n"
            f"{text}\n\n"
        )        
    # 这里用一个临时文件存储字幕，就不保存到本地，运行结束之后直接删掉
    temp_srt = 'temp_subtitle.srt'
    with open(temp_srt,"w",encoding='utf-8') as f:
        for item in srt_content:
            f.write(item)

    ffmpeg_cmd = (
        f'ffmpeg -i "{input_video}" -vf '
        f'"subtitles={temp_srt}:force_style=\'FontName={font_name},FontSize={font_size},PrimaryColour={font_color},Alignment=2,MarginV=20" ' 
        f'-c:a copy -y "{output_video}"'
    )
    subprocess.run(ffmpeg_cmd)
    os.remove(temp_srt)

if __name__ == '__main__':
    # rotate()
    # cut()
    # adjust_brightness(brightness=50,contrast=1)
    # sketch_effect()
    # detect_people_in_photos(r'F:\VOCtrainval_11-May-2012\JPEGImages',r'F:\VOCtrainval_11-May-2012\Output',confidence_threshold=0.6)
    # img_to_video(r'F:/VOCtrainval_11-May-2012/JPEGImages',r'E:/bgMusic.wav','F:/VOCtrainval_11-May-2012/FinalOutput.mp4',transition='fade')
    subtitles = {
        "00:00:05-00:00:10": "第一段字幕：欢迎观看！",
        "00:00:15-00:00:20": "第二段字幕：这是一个示例视频。"
    }
    add_captions(
        input_video="F:/VOCtrainval_11-May-2012/FinalOutput.mp4",
        output_video="F:/VOCtrainval_11-May-2012/FinalOutput2.mp4",
        subtitles_dict=subtitles,
        font_name="Arial",
        font_size=18,
        font_color="&H00FFFFFF",
        position="bottom"
    )