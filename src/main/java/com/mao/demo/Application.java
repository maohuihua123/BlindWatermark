package com.mao.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

@SpringBootApplication
public class Application {

    private static final String libName = "opencv_java452.dll";
    private static final String libPath = "lib/opencv_java452.dll";

    public static void main(String[] args){
        // 1.开发时可以如此加载动态库
        URL url = ClassLoader.getSystemResource("lib/opencv_java452.dll");
        System.load(url.getPath());
        // 2、打包部署时使用下面方法
//        loadLib();
        SpringApplication.run(Application.class, args);

    }

    /**
     * SpringBoot打包后，就是一个独立的文件。
     * 使用ClassPathResource读取classpath下的lib文件，
     * 然后copy到本地磁盘，再从文件系统去加载。
     * 注：（仅在Windows 10下测试）
     */
    public static void loadLib() {
        try {
            // 读取Resource下的动态库
            ClassPathResource classPathResource = new ClassPathResource(libPath);
            InputStream in = classPathResource.getInputStream();
            // 将动态库提取到临时文件目录 C:/Users/user/AppData/Local/Temp/opencv_java452.dll
            String nativeTempDir = System.getProperty("java.io.tmpdir");
            File libFile = new File(nativeTempDir+ File.separator + libName);
            // 如果临时文件目录不存在动态库，则进行拷贝
            if (!libFile.exists()){
                FileOutputStream fos = new FileOutputStream(libFile);
                byte[] buffer = new byte[in.available()];
                int readLength = in.read(buffer);
                fos.write(buffer);
                fos.flush();
                fos.close();
            }
            in.close();
            System.load(libFile.getAbsolutePath());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
