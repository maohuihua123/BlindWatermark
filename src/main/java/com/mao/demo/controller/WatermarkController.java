package com.mao.demo.controller;

import com.mao.demo.utils.QRCodeUtils;
import com.mao.demo.utils.WaterMarkDCT;
import com.mao.demo.utils.WaterMarkDFT;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

@Controller
@RequestMapping("/watermark")
public class WatermarkController {

    @PostMapping(value = "/embed")
    @ResponseBody
    public void addWaterMark(MultipartFile image, boolean isText, String watermark, HttpServletResponse response)  {
        // 1、获取文件名后缀
        String fileName = image.getOriginalFilename();
        String[] split = fileName.split("\\.");
        // 2、创建临时文件
        String prefix = "image";
        String suffix = ".png";
        String imageSuffix = "." + split[split.length-1];
        try {
            // 3、输入图片、输出图片、二维码图片的临时文件
            File tempInputFile = File.createTempFile(prefix, imageSuffix);
            File tempOutputFile = File.createTempFile(prefix,suffix);
            File tempOutputQRFile = File.createTempFile(prefix,suffix);
            // 临时存储输入文件
            image.transferTo(tempInputFile);
            String imagePath = tempInputFile.getAbsolutePath();
            String outputPath = tempOutputFile.getAbsolutePath();
            String outputQR = tempOutputQRFile.getAbsolutePath();
            // 开始加水印 文本则使用DFT, 否则使用DCT
            if (isText){
                WaterMarkDFT.embed(imagePath, watermark, outputPath);
            }else{
                // 将水印内容转换成二维码（生成默认的100*100尺寸的二维码）
                new QRCodeUtils().QREncode(watermark, outputQR);
                WaterMarkDCT.embed(imagePath, outputQR, outputPath);
            }
            // 返回处理后的文件
            sendImageBack(response,outputPath);
            // 5.删除临时文件
            tempInputFile.delete();
            tempOutputFile.delete();
            tempOutputQRFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping(value = "/extract",produces = {MediaType.IMAGE_PNG_VALUE})
    @ResponseBody
    public byte[] getWaterMark(MultipartFile picture, boolean isText)  {
        // 获取文件名后缀
        String fileName = picture.getOriginalFilename();
        String[] split = fileName.split("\\.");
        // 临时文件前缀和后缀
        String prefix = "temp";
        String suffix = ".png";
        String imageSuffix = "." + split[split.length-1];
        byte[] bytes = null;
        try {
            File tempInputFile = File.createTempFile(prefix, imageSuffix);
            File tempOutputFile = File.createTempFile(prefix,suffix);
            picture.transferTo(tempInputFile);
            String imagePath = tempInputFile.getAbsolutePath();
            String outputPath = tempOutputFile.getAbsolutePath();
            if (isText){
                WaterMarkDFT.extract(imagePath, outputPath);
            }else{
                WaterMarkDCT.extract(imagePath, outputPath);
            }
            // 返回图片
            File file = new File(outputPath);
            FileInputStream inputStream = new FileInputStream(file);
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, inputStream.available());
            // 关闭流、删除临时文件
            inputStream.close();
            tempInputFile.delete();
            tempOutputFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    private void sendImageBack(HttpServletResponse response, String filePath) throws Exception{
        File file = new File(filePath);
        // 1、设置response 响应头
        // 设置页面不缓存,清空buffer
        response.reset();
        // 字符编码
        response.setCharacterEncoding("UTF-8");
        // 二进制传输数据
        response.setContentType("multipart/form-data");
        // 设置响应头
        response.setHeader("Content-Disposition", "attachment;fileName="+ URLEncoder.encode(file.getName(), "UTF-8"));
        // 2、 读取文件--输入流
        InputStream input=new FileInputStream(file);
        // 3、 写出文件--输出流
        OutputStream out = response.getOutputStream();
        byte[] buff =new byte[1024];
        int length;
        // 4、执行写出操作
        while((length= input.read(buff))!= -1){
            out.write(buff, 0, length);
            out.flush();
        }
        out.close();
        input.close();
    }
}
