import os
import urllib.request
import tarfile
import zipfile

# 设定下载的 URL 和目标文件夹
url = 'http://host.robots.ox.ac.uk/pascal/VOC/voc2012/VOCtrainval_11-May-2012.tar'  # 修改为实际的 tar 文件下载链接
dataset_dir = 'F:/pascal_voc'


# 下载并解压数据集
def download_voc(url, dataset_dir):
    if not os.path.exists(dataset_dir):
        os.makedirs(dataset_dir)

    # 假设下载的是 tar 文件
    tar_file_path = os.path.join(dataset_dir, 'VOC2012.tar')

    # 下载数据集
    urllib.request.urlretrieve(url, tar_file_path)
    print(f"Downloaded to {tar_file_path}")

    # 转换 tar 文件为 zip 文件
    zip_file_path = os.path.join(dataset_dir, 'VOC2012.zip')
    with tarfile.open(tar_file_path, 'r') as tar:
        tar.extractall(path=dataset_dir)  # 提取 tar 文件的内容
        print(f"Extracted tar to {dataset_dir}")

    # 将 tar 文件的内容重新打包成 zip 格式
    with zipfile.ZipFile(zip_file_path, 'w', zipfile.ZIP_DEFLATED) as zipf:
        for root, dirs, files in os.walk(dataset_dir):
            for file in files:
                file_path = os.path.join(root, file)
                zipf.write(file_path, os.path.relpath(file_path, dataset_dir))  # 添加文件到 zip

    print(f"Created zip file at {zip_file_path}")

    # 删除原 tar 文件
    os.remove(tar_file_path)
    print(f"Deleted original tar file at {tar_file_path}")

    # 解压 zip 文件
    with zipfile.ZipFile(zip_file_path, 'r') as zip_ref:
        zip_ref.extractall(dataset_dir)
        print(f"Extracted zip to {dataset_dir}")


# 调用下载函数
download_voc(url, dataset_dir)
