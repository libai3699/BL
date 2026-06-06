package com.gp.common.base.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 图片消息加密类
 */
public class SecureImageSteganography {

    // 使用Hutool AES加密
    public static String encrypt(String data, String key) {
        AES aes = SecureUtil.aes(key.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(aes.encrypt(data));
    }

    // 使用Hutool AES解密
    public static String decrypt(String encryptedData, String key) {
        AES aes = SecureUtil.aes(key.getBytes(StandardCharsets.UTF_8));
        return aes.decryptStr(Base64.getDecoder().decode(encryptedData));
    }

    // 嵌入加密文本到图像（分散LSB写入）
    public static void embedEncryptedText(String text, String password, String outputPath) throws Exception {
//        String encrypted = encrypt(text, password);
        String encrypted = text;
        StringBuilder binary = new StringBuilder();
        for (char c : encrypted.toCharArray()) {
            binary.append(String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'));
        }
        binary.append("00000000");

        int width = 300, height = 300;
        //自制图片
        BufferedImage image;
        //通过某个图片进行伪装
        String inputImagePath = "logo.png";
        if (StrUtil.isNotBlank(inputImagePath)) {
            image = ImageIO.read(new File(inputImagePath));
        } else {
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    image.setRGB(x, y, 0xFFFFFF);
                }
            }
        }

        int step = 3;
        for (int i = 0; i < binary.length(); i++) {
            int pixelIndex = i * step;
            int x = pixelIndex % width;
            int y = pixelIndex / width;
            if (y >= height) break;

            int rgb = image.getRGB(x, y);
            int r = (rgb >> 16) & 0xFF;
            int g = (rgb >> 8) & 0xFF;
            int b = rgb & 0xFF;

            r = (r & 0xFE) | (binary.charAt(i) - '0');

            int newRgb = (r << 16) | (g << 8) | b;
            image.setRGB(x, y, newRgb);
        }

        ImageIO.write(image, "png", new File(outputPath));
        System.out.println("嵌入成功，图像保存至: " + outputPath);
    }

    // 提取加密文本并解密
    public static String extractDecryptedText(String imagePath, String password) throws Exception {
        BufferedImage image = ImageIO.read(new File(imagePath));
        StringBuilder binary = new StringBuilder();
        int width = image.getWidth();
        int height = image.getHeight();
        int step = 3;

        for (int i = 0; ; i++) {
            int pixelIndex = i * step;
            int x = pixelIndex % width;
            int y = pixelIndex / width;
            if (y >= height) break;

            int rgb = image.getRGB(x, y);
            int r = (rgb >> 16) & 0xFF;
            binary.append(r & 1);

            if (binary.length() % 8 == 0) {
                String lastByte = binary.substring(binary.length() - 8);
                if ("00000000".equals(lastByte)) break;
            }
        }

        StringBuilder encryptedText = new StringBuilder();
        for (int i = 0; i < binary.length() - 8; i += 8) {
            int charCode = Integer.parseInt(binary.substring(i, i + 8), 2);
            encryptedText.append((char) charCode);
        }

//        return decrypt(encryptedText.toString(), password);
        return encryptedText.toString();
    }

    public static void main(String[] args) throws Exception {
        String apiDomain = "https://gateway.ycleex.com";
        String password = "mtS47wRvdE6vyCIalSpW+w=="; // Hutool AES 密钥必须为16位
        String imagePath = "secure_api_image.png";

        // 嵌入
        embedEncryptedText(apiDomain, password, imagePath);

        // 提取并解密
        String result = extractDecryptedText(imagePath, password);
        System.out.println("解密后结果: " + result);
    }
}
