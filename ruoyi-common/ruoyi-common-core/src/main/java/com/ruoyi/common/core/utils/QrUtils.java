package com.ruoyi.common.core.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class QrUtils {

    public static String genQr(String content, InputStream logoSteam) throws Exception {

        //设置二维码宽
        int width = 400;
        //设置二维码高
        int height = 400;
        //设置二维码的后缀名称
        String format = "png";
        //设置map集合要往二维码内添加的参数
        @SuppressWarnings("rawtypes")
        Map map = new HashMap();
        //设置二维码的级别
        map.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        //设置二维码中文本的编码格式
        map.put(EncodeHintType.CHARACTER_SET, "utf-8");
        //设置二维码的外边框
        map.put(EncodeHintType.MARGIN, 5);

        //创建生成二维码对象，调用方法将所需要的参数放入
        BitMatrix bm = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height,map);
        //创建path对象将物理地址放到file文件内，然后生成path对象
        //使用writeToPath方法调用下载，里面的参数是下载的对象照片，下载的后缀名称，下载的物理路径地址
        MatrixToImageConfig matrixToImageConfig = new MatrixToImageConfig(0xFF000001, 0xFFFFFFFF);
        BufferedImage src = MatrixToImageWriter.toBufferedImage(bm, matrixToImageConfig);

        writeLogo(src, logoSteam);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(src, format, out);
        out.close();

        return "data:image/png;base64," + Base64.encodeBase64String(out.toByteArray());
    }

    public static String genAssetQr(Long assetId, String assetName, String sku) throws Exception {

        //设置二维码宽
        int width = 400;
        //设置二维码高
        int height = 400;
        //设置二维码的后缀名称
        String format = "png";
        //设置map集合要往二维码内添加的参数
        @SuppressWarnings("rawtypes")
        Map map = new HashMap();
        //设置二维码的级别
        map.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        //设置二维码中文本的编码格式
        map.put(EncodeHintType.CHARACTER_SET, "utf-8");
        //设置二维码的外边框
        map.put(EncodeHintType.MARGIN, 5);

        //创建生成二维码对象，调用方法将所需要的参数放入
        BitMatrix bm = new MultiFormatWriter().encode(assetId.toString(), BarcodeFormat.QR_CODE, width, height,map);
        //创建path对象将物理地址放到file文件内，然后生成path对象
        //使用writeToPath方法调用下载，里面的参数是下载的对象照片，下载的后缀名称，下载的物理路径地址
        BufferedImage src = MatrixToImageWriter.toBufferedImage(bm);


        //如果有中文，建议使用gbk；否则容易出现在ide中运行好用，将程序打包后无法正常写入中文。
        Color color = Color.BLACK;
        int fontSize = 20;
        int imageW = src.getWidth(null);
        int imageH = src.getHeight(null);
        BufferedImage image = new BufferedImage(imageW, imageH, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.createGraphics();
        g.drawImage(src, 0, 0, imageW, imageH, null);
        //设置画笔的颜色
        g.setColor(color);
        //设置字体
        Font font = new Font("宋体", Font.BOLD, fontSize);
        int startX = 80;
        int startY = 350;
        g.setFont(font);
        g.drawString(assetName, startX, startY);
        g.drawString(sku, startX, startY+30);
        g.dispose();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, format, out);
        out.close();

        return "data:image/png;base64," + Base64.encodeBase64String(out.toByteArray());
    }


    public static void main(String[] args) throws Exception {
        String id = "123";

        String assetName = new String("笔记本电脑 Z470".getBytes("gbk"),"gbk");
        String sku = new String("8G 16核 600G存储".getBytes("gbk"),"gbk");

        //设置二维码宽
        int width = 400;
        //设置二维码高
        int height = 400;
        //设置二维码的后缀名称
        String format = "png";
        //设置map集合要往二维码内添加的参数
        @SuppressWarnings("rawtypes")
        Map map = new HashMap();
        //设置二维码的级别
        map.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        //设置二维码中文本的编码格式
        map.put(EncodeHintType.CHARACTER_SET, "utf-8");
        //设置二维码的外边框
        map.put(EncodeHintType.MARGIN, 5);

        //创建生成二维码对象，调用方法将所需要的参数放入
        BitMatrix bm = new MultiFormatWriter().encode(id, BarcodeFormat.QR_CODE, width, height,map);
        //创建path对象将物理地址放到file文件内，然后生成path对象
        //使用writeToPath方法调用下载，里面的参数是下载的对象照片，下载的后缀名称，下载的物理路径地址
        MatrixToImageConfig matrixToImageConfig = new MatrixToImageConfig(0xFF000001, 0xFFFFFFFF);
        BufferedImage src = MatrixToImageWriter.toBufferedImage(bm, matrixToImageConfig);

//        //如果有中文，建议使用gbk；否则容易出现在ide中运行好用，将程序打包后无法正常写入中文。
//        Color color = Color.BLACK;
//        int fontSize = 20;
//        int imageW = src.getWidth(null);
//        int imageH = src.getHeight(null);
//        BufferedImage image = new BufferedImage(imageW, imageH, BufferedImage.TYPE_INT_RGB);
//        Graphics g = image.createGraphics();
//        g.drawImage(src, 0, 0, imageW, imageH, null);
//        //设置画笔的颜色
//        g.setColor(color);
//        //设置字体
//        Font font = new Font("宋体", Font.BOLD, fontSize);
//        int startX = 80;
//        int startY = 350;
//        g.setFont(font);
//        g.drawString(assetName, startX, startY);
//        g.drawString(sku, startX, startY+30);
//        g.dispose();

        String qrFile = "target/sandbox/tables/chengpijian.png";
        File file = new File(qrFile);
        file.getParentFile().mkdirs();
        src.setRGB(1,1,1);
        writeLogo(src, new FileInputStream(new File("/workspace/ideaspace/cqazxt_server/ruoyi-modules/ruoyi-system/src/main/resources/icon/logo.png")));
        FileOutputStream out = new FileOutputStream(qrFile);
        ImageIO.write(src, "JPEG", out);
        out.close();

        System.out.println("二维码添加文本成功");
    }

    private static void writeLogo(BufferedImage src, InputStream logoSteam) throws IOException {
        Graphics g = src.createGraphics();

        Image logo = ImageIO.read(logoSteam);
        g.drawImage(logo, src.getWidth()/2 -45, src.getHeight()/2 -45, 90, 90, null);
        g.dispose();
        src.flush();
    }
}
