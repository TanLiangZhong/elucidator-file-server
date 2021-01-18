# 文件服务器

### 技术栈

* SpringBoot
* MongoDB
* JDK 11
* SpringData MongoDB

### 功能描述

* 单文件上传
* 批量上传
* 分片上传
* 文件预览 仅支持,浏览器预览支持的文件格式
* 文件下载
* 断点续传上传 TODO
* 断点续传下载 TODO

### MongoDB GridFS 简介
> * GridFS是用于存储和检索超过16 MB BSON文档大小限制的文件
> * GridFS 会将大文件对象分割成多个小的chunk(文件片段),一般为256k/个,每个chunk将作为MongoDB的一个文档(document)被存储在chunks集合中。


### 何时使用GridFS
> * 如果文件系统限制了目录中文件的数量，则可以使用GridFS来存储所需数量的文件。
> * 当您要访问大文件部分的信息而不必将整个文件加载到内存中时，可以使用GridFS来调用文件的某些部分，而无需将整个文件读入内存。
> * 当您想要使文件和元数据自动同步并在多个系统和设施中部署时，可以使用GridFS。使用地理上分散的副本集时，MongoDB可以自动将文件及其元数据分发到许多 mongod实例和设施。
> * 如果文件都小于16 MB的限制，请考虑将每个文件存储在单个文档中，而不要使用GridFS。您可以使用BinData数据类型存储二进制数据。

### 实现思路
* 使用 MongoDB GridFS 存储文件
* 使用 SpringBootDataMongoDB 读写GridFS.
* 通过流的方式可以对文件预览或下载








