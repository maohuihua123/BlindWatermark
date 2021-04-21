# 图片盲水印系统

*  **体验：**[点击直达](http://layui.pearadmin.com)  

## 添加水印

* **文字水印：不支持中文字符**
* **二维码水印：将内容转化成二维码，再加入到图片中**

![alt text](docs/1.jpg)

## 提取水印

* **输入加了水印图像**
* **选择之前加的水印的类别**

![alt text](docs/2.jpg)

## 效果展示

* **原图**

  ![alt text](docs/source.jpg)

* **加文字水印**

  ![alt text](docs/embed1.png)

* **加二维码水印**

  ![alt text](docs/embed2.png)

* **提取文字水印**

  ![alt text](docs/extract.png)

* 二维码水印

  ![alt text](docs/extract1.png)

## 原理

* **文字水印：DFT**   
* **二维码水印：DCT**

## 其他

* 输出结果为PNG格式时效果最好

## 想法

**由于二维码是二值图，适合通过DCT嵌入到图片中；如果想嵌入其他非二维码图片，必须得把图片转化或者压缩成二值图。**

并且在底层存储上，均是由二进制表示，因此可以将图片转化成二进制串，构建二值矩阵。（**待实验**）

## TODO

> [1] A blind Robust Image Watermarking Approach exploiting the DFT Magnitude

该论文指出，**相位**相对于**幅度**更重要，**图像相位所携带的信息似乎比幅度重要得多。** 因此，选择在**幅度信息中插入水印**，因为它不会影响图像质量。

![image-20210421154615001](docs/3.png)

![](docs/4.jpg)

![](docs/5.jpg)

![](docs/6.jpg)