现在utils里面，图片会直接用jpg的格式编码，转换成字节流返回给后端。

现在图片没有进行压缩，都是直接传过去，但是后面视频可能需要进行压缩之后再传

因为临时文件无法访问的问题还没有解决，所以视频现在还是直接存储到本地。或者是专门开一个文件夹来存储视频，因为后端需要给到最终视频的输出路径，直接到输出路径去取也可以。
