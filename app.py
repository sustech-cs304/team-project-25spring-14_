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
        _,img_encoded = cv2.imencode('.jpg', img)
        return Response(img_encoded.tobytes(), mimetype='image/jpeg')
    except Exception as e:
        print(e)

@app.route('/cut')
def cut_app():
    img_path = request.args.get('img_path')
    region = request.args.get('region')
    if not img_path:
        return 'ERROR :Image_path not provided'
    if not os.path.exists(img_path):
        return 'ERROR :Image_path dose not exists'
    try:
        img = cut(img_path, region)
        _,img_encoded = cv2.imencode('.jpg', img)
        return Response(img_encoded.tobytes(), mimetype='image/jpeg')
    except Exception as e:
        print(e)

@app.route('/adjust_brightness')
def adjust_brightness_app():
    img_path = request.args.get('img_path')
    brightness = request.args.get('brightness')
    contrast = request.args.get('contrast')
    if not img_path:
        return 'ERROR :Image_path not provided'
    if not os.path.exists(img_path):
        return 'ERROR :Image_path dose not exists'
    try:
        img = adjust_brightness(img_path,brightness,contrast)
        _,img_encoded = cv2.imencode('.jpg', img)
        return Response(img_encoded.tobytes(), mimetype='image/jpeg')
    except Exception as e:
        print(e)

@app.route('/remove_object')
def remove_object_app():
    img_path = request.args.get('img_path')
    mask_region = request.args.get('mask_region')
    if not img_path:
        return 'ERROR :Image_path not provided'
    if not os.path.exists(img_path):
        return 'ERROR :Image_path dose not exists'
    try:
        img = remove_object(img_path, mask_region)
        _,img_encoded = cv2.imencode('.jpg', img)
        return Response(img_encoded.tobytes(), mimetype='image/jpeg')
    except Exception as e:
        print(e)

@app.route('/sketch_effect')
def sketch_app():
    img_path = request.args.get('img_path')
    if not img_path:
        return 'ERROR :Image_path not provided'
    if not os.path.exists(img_path):
        return 'ERROR :Image_path dose not exists'
    try:
        img = sketch_effect(img_path)
        _,img_encoded = cv2.imencode('.jpg', img)
        return Response(img_encoded.tobytes(), mimetype='image/jpeg')
    except Exception as e:
        print(e)

@app.route('/detect_face',methods=['POST'])
def detect_face_app():
    input_dir = request.form.get('input_dir')
    output_dir = request.form.get('output_dir')
    confidence_threshold = request.form.get('confidence_threshold')
    confidence_threshold = float(confidence_threshold)  # 转换成浮点数传进去
    if not input_dir:
        return 'ERROR :Image_path not provided'
    if not os.path.exists(input_dir):
        return 'ERROR :Image_path dose not exists'
    try:
        detect_object_in_photos(input_dir, output_dir, confidence_threshold)
    except Exception as e:
        print(e)

@app.route('/image_to_video',methods=['POST'])
def image_to_video_app():
    img_folder = request.form.get('img_folder')
    audio_file = request.form.get('audio_file')
    final_output_file = request.form.get('final_output_file')
    transition = request.form.get('transition')
    fps = request.form.get('fps')
    if not img_folder:
        return 'ERROR :Image_path not provided'
    if not os.path.exists(img_folder):
        return 'ERROR :Image_path dose not exists'
    try:
        img_to_video(img_folder, audio_file, final_output_file, transition, fps)
    except Exception as e:
        print(e)

@app.route('add_captions',methods=['POST'])
def add_captions_app():
    input_video = request.form.get('input_video')
    output_video = request.form.get('output_video')
    subtitles_dict = request.form.get('subtitles_dict')
    font_name = request.form.get('font_name')
    font_size = request.form.get('font_size')
    font_color = request.form.get('font_color')
    if not input_video:
        return 'ERROR :Image_path not provided'
    if not os.path.exists(input_video):
        return 'ERROR :Image_path dose not exists'
    try:
        temp_out = add_captions(input_video,subtitles_dict,font_name,font_size,font_color)  # 这里的dict需要传进去一个字典，但是这里是一个字符串，到时候看怎么转成字典
        return send_file
    except Exception as e:
        print(e)

@app.route('ai_classify_image')
def ai_classify_image_app():
    img_path = request.args.get('img_path')
    if not img_path:
        return 'ERROR :Image_path not provided'
    if not os.path.exists(img_path):
        return 'ERROR :Image_path dose not exists'
    try:
        result = ai_classify_image(img_path)
        return jsonify({'detect_class' : result})
    except Exception as e:
        print(e)
if __name__ == '__main__':
    app.run()