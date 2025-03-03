#### construct a simple database structue, of course not complete.
#### create a basic springboot structure, and copy the structure into the txt file
#### done the entity with ai & checked already, and do some basic operator in database and copy it into the mapper

#### 记录
##### 关于DTO和VO
DTO和VO都是向其他层传递信息，但是DTO通常是可以对不同对象通用的，且传递的都是实体类定义的基本信息，而VO是根据每个不同的对象进行计算和设计，所以每个VO不一样，也不能通用对

DTO 和 struct 在概念上是类似的，因为它们都用于封装数据并通过网络、API、或不同的系统层之间进行传输。也就是说我们有了DTO在不同层之间传递entity对象的时候就不用麻烦的一个一个传递对象的值，而是传递DTO，类似结构体，整题传递

VO 和 DTO 的主要区别是VO里变传递的东西是通过我们定义出来的，可以通过计算，拼接等，而DTO传递的大多数是我们在entity中已经定义好的
##### 关于此次修改（3.4日）
首先实现了photo 的系列，但是VO没有做，因为还不清楚VO要传递什么。 做了的有controller,service, dto, 目前只实现了上传和删除。（后续可能会加上下载等方法，前端可以通过controller中的getpostmapping后面的地址调用）
## 需要说明的是ai生成了大部分，我只负责让程序能够跑起来，还没有测试。
#### 对于配置的修改分布在了：

pom.xml:增加了依赖

application.properties：最后增加了地址映射。可以改成自己储存的地址,如果没改，会自动在当前的album文件夹下创建uploads，更改了file.upload.path和file.thumbnail.path，如果没有也会自动创建photo和thumbnail文件夹







