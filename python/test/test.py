import cv2
import os
import subprocess

output_file = 'F:/VOCtrainval_11-May-2012/Output.mp4'
final_output_file = 'F:/VOCtrainval_11-May-2012/FinalOutput.mp4'
audio_file = "E:/bgMusic.wav"

fourcc = cv2.VideoWriter_fourcc(*'mp4v')
fps = 25  
frame_size = (640, 480)  # 要注意resize之后是（480,640），因为传进去的是（width，height）
image_folder = "F:/VOCtrainval_11-May-2012/JPEGImages"
transiton = 'zoom'
video_writer = cv2.VideoWriter(output_file, fourcc, fps, frame_size)

image_files = sorted([f for f in os.listdir(image_folder) if f.endswith(('.png', '.jpg'))])
image_files = image_files[:100]

if transiton == '':  # 没有特效的
    for i in range(len(image_files) - 1):
        img_path = os.path.join(image_folder, image_files[i])
        img = cv2.imread(img_path)
        img = cv2.resize(img, frame_size)
        for _ in range(fps):
            video_writer.write(img)
elif transiton == 'fade':  # 渐变特效
    for i in range(len(image_files) - 1):
        img_path1 = os.path.join(image_folder, image_files[i])
        img_path2 = os.path.join(image_folder, image_files[i + 1])
        
        img1 = cv2.imread(img_path1)
        img2 = cv2.imread(img_path2)
        if img2 is None:  # 已经到了最后一张单独加上去
            continue

        img1 = cv2.resize(img1, frame_size)
        img2 = cv2.resize(img2, frame_size)

        for _ in range(fps):  # 静态帧，一秒25张
            video_writer.write(img1)
    
        num_transitions = fps  # 1秒的过渡帧数
        for j in range(num_transitions):
            alpha = j / float(num_transitions)
            blended = cv2.addWeighted(img1, 1 - alpha, img2, alpha, 0)
            video_writer.write(blended)
    for _ in range(fps):  
        video_writer.write(img2)
elif transiton == 'slide':  # 滑动特效
    for i in range(len(image_files) - 1):
        img_path1 = os.path.join(image_folder, image_files[i])
        img_path2 = os.path.join(image_folder, image_files[i + 1])
        
        img1 = cv2.imread(img_path1)
        img2 = cv2.imread(img_path2)
        img1 = cv2.resize(img1, frame_size)
        img2 = cv2.resize(img2, frame_size)
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
elif transiton == 'zoom':
    col_middle = frame_size[0]//2
    row_middle = frame_size[1]//2
    for i in range(len(image_files) - 1):
        img_path1 = os.path.join(image_folder, image_files[i])
        img_path2 = os.path.join(image_folder, image_files[i + 1])
        
        img1 = cv2.imread(img_path1)
        img2 = cv2.imread(img_path2)
        img1 = cv2.resize(img1, frame_size)
        img2 = cv2.resize(img2, frame_size)
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



video_writer.release()
print("Video saved successfully.")

ffmpeg_cmd = [  # 用ffmpeg添加音频，但是这里得先生成视频再加音频
    "ffmpeg",
    "-i", output_file,
    "-i", audio_file,
    "-c:v", "copy",
    "-c:a", "aac",
    "-strict", "experimental",
    "-shortest",
    final_output_file
]

subprocess.run(ffmpeg_cmd)
os.remove(output_file)
print("Final video with audio created.")
