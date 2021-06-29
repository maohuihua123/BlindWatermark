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

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.*;
import java.net.URLEncoder;

@Controller
@RequestMapping("/watermark")
public class WatermarkController {

    @PostMapping(value = "/embed")
    @ResponseBody
    public void addWaterMark(MultipartFile image, boolean isText, String watermark, HttpServletResponse response)  {
        try {
            // 检查上传的图片，如果不符合要求 则终止
            if (checkImage(image, isText)) {
                System.out.println("图片格式不正确或不符合要求");
                return;
            }
            // 1、获取文件名后缀
            String fileName = image.getOriginalFilename();
            // 2、创建临时文件：输入图片、输出图片、二维码图片的临时文件
            File tempInputFile = File.createTempFile("image", fileName);
            File tempOutputFile = File.createTempFile("image",".png");
            File tempOutputQRFile = File.createTempFile("image",".png");

            image.transferTo(tempInputFile);
            // 3.获取文件路径
            String imagePath = tempInputFile.getAbsolutePath();
            String outputPath = tempOutputFile.getAbsolutePath();
            String outputQR = tempOutputQRFile.getAbsolutePath();
            // 4.开始加水印 文本则使用DFT, 否则使用DCT
            if (isText){
                WaterMarkDFT.embed(imagePath, watermark, outputPath);
            }else{
                // 将水印内容转换成二维码（生成默认的100*100尺寸的二维码）
                new QRCodeUtils().QREncode(watermark, outputQR);
                WaterMarkDCT.embed(imagePath, outputQR, outputPath);
            }
            // 5.返回处理后的文件
            download(response,outputPath);
            // 6.删除临时文件
            boolean delete = tempInputFile.delete();
            boolean delete1 = tempOutputFile.delete();
            boolean delete2 = tempOutputQRFile.delete();
            if (delete && delete1 && delete2){
                System.out.println("临时文件已删除");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping(value = "/extract",produces = {MediaType.IMAGE_PNG_VALUE})
    @ResponseBody
    public byte[] getWaterMark(MultipartFile picture, boolean isText)  {
        try {
            if (checkImage(picture, isText)) {
                String str = "图片格式不正确或不符合要求";
                System.out.println(str);
                return null;
            }

            String fileName = picture.getOriginalFilename();
            File tempInputFile = File.createTempFile("temp", fileName);
            File tempOutputFile = File.createTempFile("temp",".png");
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
            byte[] bytes  = new byte[inputStream.available()];
            int length = inputStream.read(bytes, 0, inputStream.available());
            // 关闭流、删除临时文件
            inputStream.close();
            boolean delete0 = tempInputFile.delete();
            boolean delete1 = tempOutputFile.delete();
            if (delete0 && delete1){
                System.out.println("临时文件已删除");
            }
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean checkImage(MultipartFile image, boolean isText) throws IOException {
        Image picture = ImageIO.read(image.getInputStream());
        // 如果不是图片, 返回false
        if (picture == null){
            return true;
        }
        // 是图片，则判断水印类别以及要求的图片尺寸
        if (isText){
            return false;
        } else {
            // (二维码水印 && 图片尺寸 >= 800 * 800 返回true)
            return (picture.getWidth(null) < 800) || (picture.getHeight(null) < 800);
        }
    }


    private void download(HttpServletResponse response, String filePath) throws Exception{
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
