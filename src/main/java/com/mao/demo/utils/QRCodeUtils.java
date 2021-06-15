package com.mao.demo.utils;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.CharacterSetECI;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class QRCodeUtils {

    // 二维码的宽度
    private int width;
    // 二维码的高度
    private int height;
    // 二维码的格式
    private String format;
    // 二维码参数
    private final Map<EncodeHintType, Object> paramMap;


    public QRCodeUtils() {
        // 1.默认尺寸
        this.width = 100;
        this.height = 100;
        // 2.默认格式
        this.format = "png";
        // 3，默认参数
        // 定义二维码的参数
        this.paramMap = new HashMap<>();
        // 设置二维码字符编码
        paramMap.put(EncodeHintType.CHARACTER_SET, CharacterSetECI.UTF8);
        // 设置二维码纠错等级
        paramMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        // 设置二维码边距
        paramMap.put(EncodeHintType.MARGIN, 1);

    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setSize(int width, int heigth) {
        this.width = width;
        this.height = heigth;
    }

    public void setParam(EncodeHintType type, Object param) {
        paramMap.put(type,param);
    }

    /**
     * 生成二维码（写入文件）
     *
     * @param content  二维码内容
     * @param outPutFile 二维码输出路径
     */
    public void QREncode(String content,String outPutFile) throws Exception {
        // 开始生成二维码
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, width, height, paramMap);
        // 导出到指定目录
        Path path = new File(outPutFile).toPath();
        MatrixToImageWriter.writeToPath(bitMatrix, format, path);
    }


    /**
     * 生成二维码（内存中）
     *
     * @param contents 二维码的内容
     * @return BufferedImage
     */
    public BufferedImage QREncode(String contents) throws Exception {
        // 开始生成二维码
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        BitMatrix bitMatrix = multiFormatWriter.encode(contents,BarcodeFormat.QR_CODE, width, height, paramMap);
        // 写入内存
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bufferedImage.setRGB(x, y, bitMatrix.get(x, y) ? 1 : 0);
            }
        }
        return bufferedImage;
    }

    /**
     * @param bufferedImage 二维码图片
     * @return 文本内容
     */
    private Result getResult(BufferedImage bufferedImage) throws NotFoundException {
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        HybridBinarizer binarizer = new HybridBinarizer(source);
        BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
        // 二维码参数
        Map hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, CharacterSetECI.UTF8);
        MultiFormatReader formatReader = new MultiFormatReader();
        return formatReader.decode(binaryBitmap, hints);
    }

    /**
     * 解析二维码
     *
     * @param filePath 待解析的二维码路径
     * @return 二维码内容
     */
    public String QRReader(String filePath) throws Exception {
        File file = new File(filePath);
        // 读取指定的二维码文件
        BufferedImage bufferedImage = ImageIO.read(file);
        Result result = getResult(bufferedImage);
        bufferedImage.flush();
        return result.getText();
    }

    /**
     * 解析二维码
     *
     * @return 二维码内容
     */
    public String QRReader(BufferedImage bufferedImage) throws Exception {
        Result result = getResult(bufferedImage);
        bufferedImage.flush();
        return result.getText();
    }
}
