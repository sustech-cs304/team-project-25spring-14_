import os
import random
import subprocess
import time
from datetime import datetime, timedelta
import requests
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

# def ai_classify_image(image_url):  # image_url 是网络图片地址
#     model = YOLO('yolov8n.pt')
#     common_choices = ['person', 'car', 'dog', 'cat', 'book', 'airplane']

#     try:
#         response = requests.get(image_url)
#         if response.status_code != 200:
#             print(f"Failed to fetch image from {image_url}, status code: {response.status_code}")
#             return ''
#         img_array = np.asarray(bytearray(response.content), dtype=np.uint8)
#         img = cv.imdecode(img_array, cv.IMREAD_COLOR)

#         img_rgb = cv.cvtColor(img, cv.COLOR_BGR2RGB)

#         result = model.predict(img_rgb, verbose=False)

#         detect = ''
#         for box in result[0].boxes:
#             class_id = int(box.cls[0].item())
#             class_name = model.names[class_id]
#             if class_name in common_choices and class_name not in detect:
#                 detect += f' {class_name}'
#         return detect.strip()

#     except Exception as e:
#         print(f"Error processing image URL {image_url}: {e}")
#         return ''

def ai_classify_image(image_path):  # 用AI来识别图片中是否有特定的物体
    model = YOLO('yolov8n.pt')
    common_choises = ['person', 'car', 'dog', 'cat', 'book', 'airplane']
    result = model.predict(image_path, verbose=False)
    detect = ''
    for box in result[0].boxes:
        class_id = int(box.cls[0].item())
        class_name = model.names[class_id]
        if class_name in common_choises and class_name not in detect:
            detect  = f'{detect} {class_name}' # 还是沿用字符串，中间加上一个空格
    return detect
    
def get_image_from_url(image_url):
    try:
        response = requests.get(image_url)
        if response.status_code == 200:
            img_array = np.asarray(bytearray(response.content), dtype=np.uint8)
            img = cv.imdecode(img_array, cv.IMREAD_COLOR)
            return img
        else:
            print(f"Error: Failed to fetch {image_url} (Status Code: {response.status_code})")
            return None
    except Exception as e:
        print(f"Error: {e} while fetching {image_url}")
        return None
    
def download_audio(audio_url):
    resp = requests.get(audio_url, stream=True)
    resp.raise_for_status()
    suffix = os.path.splitext(audio_url)[1] or '.wav'
    tmp_name = f"temp_audio_{int(time.time())}_{random.randint(1000,9999)}{suffix}"
    with open(tmp_name, 'wb') as f:
        for chunk in resp.iter_content(8192):
            f.write(chunk)
    return tmp_name

def img_to_video(image_folder, audio_file,transition='', fps=25):  # 跟上面的方法一致，不过这个可以添加图片切换时候的特效
    # 这里直接传进来的是一个列表，然后里面包含了所有图片的地址
    fourcc = cv.VideoWriter_fourcc(*'mp4v')
    frame_size = (640, 480)  # 要注意resize之后是（480,640），因为传进去的是（width，height）
    temp_output = f'temp_{int(time.time())}_{random.randint(1000, 9999)}.mp4'  # 确保每一个请求都会有唯一的临时文件名
    video_writer = cv.VideoWriter(temp_output, fourcc, fps, frame_size)

    image_files = image_folder
    if transition == '':  # 没有特效的
        for i in range(len(image_files) - 1):
            img_path = image_files[i]
            # img = cv.imread(img_path)
            img = get_image_from_url(img_path)
            if img is None:
                continue
            img = cv.resize(img, frame_size)
            for _ in range(fps):
                video_writer.write(img)
    elif transition == 'fade':  # 渐变特效
        for i in range(len(image_files) - 1):
            img_path1 = image_files[i]
            img_path2 = image_files[i + 1]

            img1 = get_image_from_url(img_path1)
            img2 = get_image_from_url(img_path2)

            if img2 is None or img1 is None:  # 已经到了最后一张单独加上去
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
            img_path1 = image_files[i]
            img_path2 = image_files[i + 1]

            img1 = get_image_from_url(img_path1)
            img2 = get_image_from_url(img_path2)

            if img2 is None or img1 is None:  # 已经到了最后一张单独加上去
                continue

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

            img1 = get_image_from_url(img_path1)
            img2 = get_image_from_url(img_path2)

            if img2 is None or img1 is None:  # 已经到了最后一张单独加上去
                continue

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
    if audio_file is None:  # 没有音频文件
        with open(temp_output, 'rb') as f:
            file_data = f.read()
        os.remove(temp_output)
        return file_data
    
    # 有音频文件，使用ffmpeg合并
    final_output_file = f'temp_{int(time.time())}_{random.randint(1000, 9999)}.mp4'
    ffmpeg_cmd = [
        "ffmpeg",
        "-i", temp_output,
        "-i", audio_file,
        "-c:v", "copy",
        "-c:a", "aac",
        "-shortest",
        final_output_file
    ]
    
    subprocess.run(ffmpeg_cmd)
    with open(final_output_file, 'rb') as f:
        file_data = f.read()
    
    # 清理临时文件
    os.remove(final_output_file)
    os.remove(temp_output)
    # 注意：这里不再删除audio_file，因为它是由调用方创建的临时文件
    # os.remove(audio_file)  # 删除这行
    
    print("Final video with audio created.")
    return file_data


def add_captions(input_video, subtitles_dict: dict, font_name='Arial', font_size=24, font_color='&H00FFFFFF'):
    """
    使用 ffmpeg 硬编码字幕到视频，支持自定义字体样式
    """
    srt_content = []
    for idx, (time_range, text) in enumerate(subtitles_dict.items(), 1):
        start_time, end_time = time_range.split("-")
        start_time = datetime.strptime(start_time, "%H:%M:%S")
        end_time = datetime.strptime(end_time, "%H:%M:%S")
        start_srt = start_time.strftime("%H:%M:%S,000")
        end_srt = (end_time - timedelta(seconds=1)).strftime("%H:%M:%S,999")
        srt_content.append(f"{idx}\n{start_srt} --> {end_srt}\n{text}\n\n")

    temp_srt = 'temp_subtitle.srt'
    with open(temp_srt, "w", encoding='utf-8') as f:
        f.writelines(srt_content)

    input_video = download_video(input_video=input_video)
    output_video = f'temp_{int(time.time())}_{random.randint(1000, 9999)}.mp4'

    subtitles_filter = (
        f"subtitles={os.path.abspath(temp_srt)}:"
        f"force_style='FontName={font_name},FontSize={font_size},"
        f"PrimaryColour={font_color},Alignment=2,MarginV=20'"
    )

    try:
        subprocess.run(
            ["ffmpeg", "-i", input_video, "-vf", subtitles_filter,
             "-c:a", "copy", "-y", output_video],
            check=True
        )
    except subprocess.CalledProcessError as e:
        print(f"FFmpeg error: {e}")
        os.remove(temp_srt)
        os.remove(input_video)
        raise

    with open(output_video, 'rb') as f:
        file_data = f.read()

    os.remove(output_video)
    os.remove(temp_srt)
    os.remove(input_video)
    return file_data

def download_video(input_video):
    local_path = f"video_{int(time.time())}_{random.randint(0,999)}.mp4"
    resp = requests.get(input_video, stream=True)
    resp.raise_for_status()
    with open(local_path, "wb") as fw:
        for chunk in resp.iter_content(chunk_size=8192):
            fw.write(chunk)
    return local_path    

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
