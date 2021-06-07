package com.mao.demo.controller;

import com.mao.demo.base.QRCodeUtils;
import com.mao.demo.watermark.WaterMarkDFT;
import com.mao.demo.watermark.WaterMarkDCT;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Controller
@RequestMapping("/watermark")
public class WatermarkController {

    @PostMapping(value = "/embed",produces = {MediaType.IMAGE_PNG_VALUE})
    @ResponseBody
    public byte[] addWaterMark(MultipartFile image, boolean isText, String watermark)  {
        // 获取文件名后缀
        String fileName = image.getOriginalFilename();
        String[] split = fileName.split("\\.");
        // 创建临时文件
        String prefix = "temp";
        String suffix = ".png";
        String imageSuffix = "." + split[split.length-1];
        byte[] bytes = null;
        try {
            File temp = File.createTempFile(prefix, imageSuffix);
            File tempOutputFile = File.createTempFile(prefix,suffix);
            File tempOutputQRFile = File.createTempFile(prefix,suffix);
            BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream(temp));
            bw.write(image.getBytes());
            String imagePath = temp.getAbsolutePath();
            String outputPath = tempOutputFile.getAbsolutePath();
            String outputQR = tempOutputQRFile.getAbsolutePath();
            if (isText){
                WaterMarkDFT.embed(imagePath, watermark, outputPath);
            }else{
                // 默认生成100*100尺寸的二维码
                new QRCodeUtils().QREncode(watermark, outputQR);
                WaterMarkDCT.embed(imagePath, outputQR, outputPath);
            }
            File file = new File(outputPath);
            FileInputStream inputStream = new FileInputStream(file);
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, inputStream.available());
            bw.close();
            inputStream.close();
            temp.delete();
            tempOutputFile.delete();
            tempOutputQRFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
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
            File temp = File.createTempFile(prefix, imageSuffix);
            File tempOutputFile = File.createTempFile(prefix,suffix);
            BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream(temp));
            bw.write(picture.getBytes());
            String imagePath = temp.getAbsolutePath();
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
            bw.close();
            inputStream.close();
            temp.delete();
            tempOutputFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }
}
