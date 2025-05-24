import ast
import io

import cv2
from flask import Flask, request, Response, jsonify, send_file
from utils import *

app = Flask(__name__)

@app.route('/rotate')  # 是否保存：都会返回一个图像的信息
def rotate_app():
    img_path = request.args.get('img_path')
    if not img_path:
        return 'ERROR :Image_path not provided'
    if not os.path.exists(img_path):
        return 'ERROR :Image_path dose not exists'
    try:
        img = rotate(img_path)
        img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
        _,img_encoded = cv2.imencode('.jpg', img)
        return Response(img_encoded.tobytes(), mimetype='image/jpeg')
    except Exception as e:
        print(e)

@app.route('/cut')
def cut_app():
    img_path = request.args.get('img_path')
    region = request.args.get('region')
    region = ast.literal_eval(region)
    if not img_path:
        return 'ERROR :Image_path not provided'
    if not os.path.exists(img_path):
        return 'ERROR :Image_path dose not exists'
    try:
        img = cut(img_path, region)
        img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
        _,img_encoded = cv2.imencode('.jpg', img)
        return Response(img_encoded.tobytes(), mimetype='image/jpeg')
    except Exception as e:
        print(e)

@app.route('/adjust_brightness')
def adjust_brightness_app():
    img_path = request.args.get('img_path')
    brightness = int(request.args.get('brightness'))
    contrast = float(request.args.get('contrast'))
    if not img_path:
        return 'ERROR :Image_path not provided'
    if not os.path.exists(img_path):
        return 'ERROR :Image_path dose not exists'
    try:
        img = adjust_brightness(img_path,brightness,contrast)
        img = cv.cvtColor(img, cv.COLOR_BGR2RGB)
        _,img_encoded = cv2.imencode('.jpg', img)
        return Response(img_encoded.tobytes(), mimetype='image/jpeg')
    except Exception as e:
        print(e)

@app.route('/denoising')
def detect_face_app():
    img_path = request.args.get('img_path')
    if not img_path:
        return 'ERROR :Image_path not provided'
    if not os.path.exists(img_path):
        return 'ERROR :Image_path dose not exists'
    try:
        img = denoising(img_path)
        img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
        _,img_encoded = cv2.imencode('.jpg', img)
        return Response(img_encoded.tobytes(), mimetype='image/jpeg')
    except Exception as e:
        print(e)

@app.route('/image_to_video', methods=['POST'])  # 改为 POST 请求
def image_to_video_app():
    data = request.json

    img_folder = data.get('img_folder')
    audio_file = data.get('audio_file', None)
    transition = data.get('transition')
    # final_output_file = data.get('final_output_file')
    fps = int(data.get('fps', 25))  # 默认 fps 为 25

    # if not img_folder or not final_output_file:
    #     return jsonify({'error': 'img_folder and final_output_file are required'}), 400

    try:
        file_data = img_to_video(img_folder, audio_file, transition, fps)  # 调用视频生成函数
        file_stream = io.BytesIO(file_data)
        return send_file(file_stream,mimetype='video/mp4',download_name="ouput.mp4")
    except Exception as e:
        print(e)
        return jsonify({'error': str(e)}), 500

@app.route('/add_captions',methods=['POST'])
def add_captions_app():
    data = request.json
    input_video = data.get('input_video')
    # output_video = data.get('output_video')
    subtitles_dict = data.get('subtitles_dict')
    font_name = data.get('font_name')
    font_size = int(data.get('font_size'))
    font_color = data.get('font_color')
    if not input_video:
        return 'ERROR :Image_path not provided'
    if not os.path.exists(input_video):
        return 'ERROR :Image_path dose not exists'
    try:
        file_data = add_captions(input_video,subtitles_dict,font_name,font_size,font_color)  # 这里的dict需要传进去一个字典，但是这里是一个字符串，到时候看怎么转成字典
        file_stream = io.BytesIO(file_data)
        return send_file(file_stream,mimetype='video/mp4',download_name="ouput.mp4")
    except Exception as e:
        print(e)

@app.route('/ai_classify_image')
def ai_classify_image_app():
    img_path = request.args.get('img_path')
    if not img_path:
        return jsonify({'error': 'Image_path not provided'}), 400
    if img_path.startswith('http://localhost:8080/uploads'):
        img_path = img_path.replace('http://localhost:8080/uploads', '/app/storage')
    
    print(f"🔍 处理图片路径: {img_path}")
    
    if not os.path.exists(img_path):
        return jsonify({'error': f'Image file does not exist: {img_path}'}), 400
    # if not os.path.exists(img_path):
    #     return 'ERROR :Image_path dose not exists'
    try:
        result = ai_classify_image(img_path)
        return jsonify({'detect_class' : result})
    except Exception as e:
        print(e)
if __name__ == '__main__':
    # app.run()
    app.run(host='0.0.0.0', port=5000, debug=False)
