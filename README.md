# shear_plate
这是一个云剪切板的推盘程序，用java写的，后续会持续更新维护，这是第二版，还不是特别完善，做个记录

 # 简介
 - 又发现在两个电脑拷贝上烦的一批（两台电脑要登陆两个微信或者qq）才能互相传文件和拷贝文件连接图片什么的。
 - 云剪切板就事让你在两台电脑或者n台电脑上拷贝贴贴就和在本机和操作是一样的，无感操作，让你在n个电脑拷贝如鱼得水，不用在通过微信,QQ去进行数据传输。
 - 目前是V2.0版本基本拷贝和通信功能全部实现，不会写代码的可能现在用着还不方便，中间还需要一个服务器才能进行通信，后续会迭代更新，做到在局域网的无感操作，外网暂时不考虑做，需要的小伙伴可以自己在源代码的基础上进行修改。
 
 # 技术介绍
 
 - java语言
 - 通信netty
 - maven
 - netty重连，心跳里面都有
 - 一共有三个项目分别是：服务端，客户端，协议
 - 服务端的主要是做了一个中转站，方便客户端与客户端之间通信，进行数据传输。
 - 客户端对本机剪切板监听，接收别的客户端数据，对剪切板操作。
 - 协议是用来规范客户端和服务端传输数据用的
 - 目前是写死的广播信号，在同一信号可以相互拷贝
 - 必须在同一广播信号才能进行拷贝
 - **代码就贴出来给大家看了，需要源码的可以去github上下载**

# 项目介绍

 1. 项目结构
 
![在这里插入图片描述](https://img-blog.csdnimg.cn/cb4f9a88a4a64e0f9306c7693de4ddfd.png)
第一个项目客户端
第二个协议
第三个服务端

 2. 客户端介绍
 ![客户端介绍](https://img-blog.csdnimg.cn/697b226661744bd3bc21e0aebf2af0bb.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MTkwNzc1NA==,size_16,color_FFFFFF,t_70)
img托盘图标
page页面
shear剪切板相关操作
socket 通信这一块
utils工具类
Start是启动入口

```java
//启动入口代码
public class Start
{
    public static ClientInfo clientInfo;
    private static final String IP = "192.168.1.75";
    private static final int PORT = 8080;
    public static final String VERSION = "2.0";

    private static void start()
    {
        //面板启动
        SwingUtilities.invokeLater(() -> new Panel().createGUI());

        //客户端连接启动
        clientInfo = new ClientInfo(IP, PORT);
        clientInfo.start();
        
        //监听剪切板
        new MonitorChangesInClipboardContent(textMsg ->
        {
            BroadcastText broadcastText = new BroadcastText();
            try
            {
                broadcastText.setText(URLEncoder.encode(textMsg, "UTF-8"));
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
            clientInfo.send(broadcastText);
        }, fileList ->
        {
            List<FileUploadEntity> fileInfos = new ArrayList<>();
            for (File file : fileList)
            {
                try
                {
                    MyFileUtils.getFiles(file, fileInfos, null);
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
            }
            BroadcastFile broadcastFile = new BroadcastFile();
            broadcastFile.setFileUploadEntities(fileInfos);
            try
            {
                broadcastFile.setFilePaths(URLEncoder.encode(MonitorChangesInClipboardContent.filePaths,"UTF-8"));
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
            broadcastFile.setFileSize(MonitorChangesInClipboardContent.fileSize);
            clientInfo.send(broadcastFile);
        });
    }

    public static void main(String[] args)
    {
        start();
    }
```

服务端启动成功
![在这里插入图片描述](https://img-blog.csdnimg.cn/c4ff9e57e55e4a648455a4a7e4130505.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MTkwNzc1NA==,size_16,color_FFFFFF,t_70)
客户端出现连接成功即可
![在这里插入图片描述](https://img-blog.csdnimg.cn/0cfb161fcf36485dadfad94fd95d5f36.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MTkwNzc1NA==,size_16,color_FFFFFF,t_70)

与服务端连接成功
![在这里插入图片描述](https://img-blog.csdnimg.cn/9a2dde967c0b42458eb423e6ad0425f3.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MTkwNzc1NA==,size_16,color_FFFFFF,t_70)
启动过程和连接基本上就这样了，下面给大家看看程序跑起来的效果图
 
 # 效果图
启动后的托盘程序，在苹果电脑在上面，window在右下角
![在这里插入图片描述](https://img-blog.csdnimg.cn/20be2e7bb57742af96cb60aca4960a02.png)
这是没用选择广播信号的效果图
![在这里插入图片描述](https://img-blog.csdnimg.cn/82f80574019c4abd80f5a46f6a1b46cd.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MTkwNzc1NA==,size_16,color_FFFFFF,t_70)
菜单
![在这里插入图片描述](https://img-blog.csdnimg.cn/20e05e046b2f426b8973fc64869df1eb.png)
广播信号切换成功
![在这里插入图片描述](https://img-blog.csdnimg.cn/4e9bca7ce36b45c4a9f79a37e53d7d75.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MTkwNzc1NA==,size_16,color_FFFFFF,t_70)
查看信号通道
![在这里插入图片描述](https://img-blog.csdnimg.cn/941c3aa0e1c54e1e85051bacc0786f8f.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MTkwNzc1NA==,size_16,color_FFFFFF,t_70)
window的也是一样就不贴太多图片了
![在这里插入图片描述](https://img-blog.csdnimg.cn/78c1dd6eb02c4702ad256e1f8aadc571.png)

![在这里插入图片描述](https://img-blog.csdnimg.cn/2adbaae034904b9f8eae552753b844c4.png)
拷贝文件的话默认在桌面有个临时文件夹，因为在mac上做不到文件设置到剪切板，目前还没有找到解决方案，只能采取这种比较陋的方式了
![在这里插入图片描述](https://img-blog.csdnimg.cn/366c835a18b94fa9803a3848e98f9e79.png)

# 操作流程
操作流程给大家介绍一下

 1. 启动服务端：看到**启动成功**的字样，就ok
 2. 启动服务端：看到**启动成功与连接成功的**字样，OK
 3. 默认是没用广播信号的，要选择一个广播信号，mac电脑鼠标左键window鼠标右键出来菜单。就是1-10还要打开和关闭的菜单，在1-10选择一个信号，看到提示框提示切换成功就ok了，第二个活第n个客户端也是一样操作，如果想确认一下信号是否切换成功，可以选择菜单的打开可以看到。（注意：两个客户端必须在相同的信号才能进行通信复制）
 4. 复制文本可以做到无感操作，和本机上是一样 ctrl+c 在 别的电脑ctrl +v可以贴出来。
 5. 复制文件，图片，文件夹，文件图片文件夹组合，可以在本机ctrl+c 在别的电脑桌面会出来一个云拷贝的文件夹。里面就是你拷贝的内容。目前做不到无感。
 6. 文件设置了100MB，不能大于这个值。
 7. 如果觉得对你有帮助请给个好评，接下来会持续更新的

# 打包exe教程
[打包exe教程](https://blog.csdn.net/m0_37701381/article/details/104163877)

# 源码地址
[源码地址github](https://github.com/LiangXiaoWei1024/shear_plate)
