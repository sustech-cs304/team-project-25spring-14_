import os
import subprocess
from datetime import datetime, timedelta

import cv2 as cv
import matplotlib.pyplot as plt
import numpy as np
from ultralytics import YOLO

"""
这个版本还是python服务器不直接对数据库以及本地文件进行修改,但是会通过springboot后端传过来的路径,直接获取服务器本地的文件
然后生成的图片和视频都直接用编码的方式回传给springboot
"""


def rotate(image_path):  # 每点击一下就逆时针旋转90度，然后判断是否需要保存
    img = cv.imread(image_path)
    img = cv.cvtColor(img, cv.COLOR_BGR2RGB)
    height = img.shape[0]
    width = img.shape[1]
    ro_matrix = cv.getRotationMatrix2D((width / 2, height / 2), 90, 1)
    img = cv.warpAffine(img, ro_matrix, (height, width))
    return img
    # plt.imshow(img)
    # plt.axis('off')
    # plt.show()


def cut(img_path, region):  # 对图片进行裁剪
    img = cv.imread(img_path)
    print(img.shape)
    img = cv.cvtColor(img, cv.COLOR_BGR2RGB)
    (x1, y1), (x2, y2) = region
    cut_img = img[x1:y1, x2:y2]
    return cut_img
    # plt.imshow(cut_img)
    # plt.axis('off')
    # plt.show()


def adjust_brightness(img_path, brightness=0, contrast=1.0):  # 调整亮度，和对比度
    img = cv.imread(img_path)
    img = cv.cvtColor(img, cv.COLOR_BGR2RGB)
    plt.figure(figsize=(8, 3))
    plt.subplot(121)
    plt.imshow(img)
    plt.subplot(122)
    img = cv.convertScaleAbs(img, alpha=contrast, beta=brightness)
    return img
    # plt.imshow(img)
    # plt.axis('off')
    # plt.show()

def ai_classify_image(image_path):  # 用AI来识别图片中是否有特定的物体
    model = YOLO('yolov8n.pt')
    common_choises = ['person', 'car', 'dog', 'cat', 'book', 'snowboard']
    result = model.predict(image_path, verbose=False)
    detect = []
    for box in result[0].boxes:
        class_id = int(box.cls[0].item())
        class_name = model.names[class_id]
        if class_name in common_choises and class_name not in detect:
            detect.append(class_name)
    return detect


def img_to_video(image_folder, audio_file, final_output_file, transition='', fps=25):  # 跟上面的方法一致，不过这个可以添加图片切换时候的特效
    # 这里直接传进来的是一个列表，然后里面包含了所有图片的地址
    fourcc = cv.VideoWriter_fourcc(*'mp4v')
    frame_size = (640, 480)  # 要注意resize之后是（480,640），因为传进去的是（width，height）
    temp_output = 'temp.mp4'
    video_writer = cv.VideoWriter(temp_output, fourcc, fps, frame_size)

    image_files = image_folder
    if transition == '':  # 没有特效的
        for i in range(len(image_files) - 1):
            img_path = image_files[i]
            img = cv.imread(img_path)
            img = cv.resize(img, frame_size)
            for _ in range(fps):
                video_writer.write(img)
    elif transition == 'fade':  # 渐变特效
        for i in range(len(image_files) - 1):
            img_path1 = image_files[i]
            img_path2 = image_files[i + 1]

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
                img[:, frame_size[0] - x_offset:] = img2[:, :x_offset]
                video_writer.write(img)
        for i in range(fps // 2):
            video_writer.write(img2)
    elif transition == 'zoom':
        col_middle = frame_size[0] // 2
        row_middle = frame_size[1] // 2
        for i in range(len(image_files) - 1):
            img_path1 = image_files[i]
            img_path2 = image_files[i + 1]

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
                col_offset = int(alpha * frame_size[0]) // 2
                row_offset = int(alpha * frame_size[1]) // 2
                img = img1.copy()
                img[row_middle - row_offset:row_middle + row_offset,
                col_middle - col_offset:col_middle + col_offset] = img2[row_middle - row_offset:row_middle + row_offset,
                                                                   col_middle - col_offset:col_middle + col_offset]
                video_writer.write(img)
        for i in range(fps // 2):
            video_writer.write(img2)

    video_writer.release()  # 释放资源，不然最后会报警告
    print("Video saved successfully.")

    # 这里用ffmpeg添加音频，需要到https://www.ffmpeg.org/ 上面下载，解压后把bin目录加到环境变量里面
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


def add_captions(input_video, output_video, subtitles_dict: dict, font_name='Arial', font_size=24, font_color='white'):
    """
    这里用ffmpeg来添加字幕，因为使用cv的话需要逐帧渲染太麻烦了，而且不易于前端的操作
    这里面的字典，key是输入的时间，value是字幕内容
    将字典转换成.srt字幕文件，将字幕硬编码到视频
    但是这里的字幕必须手动加入，还没有能够自动加入的库
    """
    srt_content = []
    for idx, (time_range, text) in enumerate(subtitles_dict.items(), 1):
        start_time, end_time = time_range.split("-")
        start_time = datetime.strptime(start_time, "%H:%M:%S")
        end_time = datetime.strptime(end_time, "%H:%M:%S")
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
    with open(temp_srt, "w", encoding='utf-8') as f:
        for item in srt_content:
            f.write(item)

    ffmpeg_cmd = (
        f'ffmpeg -i "{input_video}" -vf '
        f'"subtitles={temp_srt}:force_style=\'FontName={font_name},FontSize={font_size},PrimaryColour={font_color},Alignment=2,MarginV=20" '
        f'-c:a copy -y "{output_video}"'
    )
    subprocess.run(ffmpeg_cmd)
    os.remove(temp_srt)


def denoising(img_path):  # 这里用锐化的效果比较明显，高斯滤波和双边滤波的效果一般

    img = cv.imread(img_path)
    img = cv.cvtColor(img, cv.COLOR_BGR2RGB)
    sharpen_kernel = np.array([[0, -1, 0], [-1, 5, -1], [0, -1, 0]])
    sharpened_img = cv.filter2D(img, -1, sharpen_kernel)
    sharpened_img = np.clip(sharpened_img, 0, 255).astype(np.uint8)
    return sharpened_img
    # plt.subplot(121)
    # plt.imshow(sharpened_img)
    # plt.axis('off')
    # plt.subplot(122)
    # plt.imshow(img)
    # plt.axis('off')
    # plt.show()


if __name__ == '__main__':
    # rotate('F:/VOCtrainval_11-May-2012/JPEGImages/2007_000027.jpg')
    # cut('./resources/lenna.jpg',((100,200),(100,200)),False)
    # adjust_brightness('./resources/lenna.jpg',False,100,1)
    # remove_object('F:/VOCtrainval_11-May-2012/JPEGImages/2007_000170.jpg',None)
    # sketch_effect('./resources/lenna.jpg',False)
    # detect_people_in_photos('F:/VOCtrainval_11-May-2012/JPEGImages','F:/VOCtrainval_11-May-2012/Output',0.6)
    image_files = sorted(
        [f for f in os.listdir('F:/VOCtrainval_11-May-2012/JPEGImages') if f.endswith(('.png', '.jpg'))])
    image_files = image_files[:100]
    image_files = [os.path.join('F:/VOCtrainval_11-May-2012/JPEGImages', x) for x in image_files]
    img_to_video(image_files, final_output_file='F:/VOCtrainval_11-May-2012/FinalOutput.mp4',
                 audio_file='E:/bgMusic.wav', transition='fade')
    # subtitles = {
    #     "00:00:05-00:00:10": "第一段字幕：欢迎观看！",
    #     "00:00:15-00:00:20": "第二段字幕：这是一个示例视频。"
    # }
    # add_captions(
    #     input_video="F:/VOCtrainval_11-May-2012/FinalOutput.mp4",
    #     output_video="F:/VOCtrainval_11-May-2012/FinalOutput_captions.mp4",
    #     subtitles_dict=subtitles,
    #     font_name="Arial",
    #     font_size=18,
    #     font_color="&H00FFFFFF"
    # )
    # play_video(temp)
    # os.remove(temp)  # 删除临时文件
    # denoising('F:/VOCtrainval_11-May-2012/JPEGImages/2007_000027.jpg')
    # black_white_filter('F:/VOCtrainval_11-May-2012/JPEGImages/2007_000042.jpg')
