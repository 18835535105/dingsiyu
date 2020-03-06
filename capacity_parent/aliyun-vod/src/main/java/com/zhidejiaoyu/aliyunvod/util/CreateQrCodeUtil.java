package com.zhidejiaoyu.aliyunvod.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * 生成多媒体url二维码
 *
 * @author: wuchenxi
 * @date: 2020/3/6 15:46:46
 */
@Slf4j
public class CreateQrCodeUtil {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 300;

    /**
     * 生成二维码
     * @param text 内容，可以是链接或者文本
     * @param path 生成的二维码位置
     */
    public static void encodeQRCode(String text, String path) {
        encodeQRCode(text, path, null, null, null);
    }

    /**
     * 生成二维码
     * @param text 内容，可以是链接或者文本
     * @param path 生成的二维码位置
     * @param width 宽度，默认300
     * @param height 高度，默认300
     * @param format 生成的二维码格式，默认png
     */
    public static void encodeQRCode(String text, String path, Integer width, Integer height, String format) {
        try {

            // 得到文件对象
            File file = new File(path);
            // 判断目标文件所在的目录是否存在
            if(!file.getParentFile().exists()) {
                // 如果目标文件所在的目录不存在，则创建父目录
                log.info("目标文件所在目录不存在，准备创建它！");
                if(!file.getParentFile().mkdirs()) {
                    log.info("创建目标文件所在目录失败！");
                    return;
                }
            }

            // 宽
            if (width == null) {
                width = WIDTH;
            }
            // 高
            if (height == null) {
                height = HEIGHT;
            }
            // 图片格式
            if (format == null) {
                format = "png";
            }

            // 设置字符集编码
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            // 生成二维码矩阵
            BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
            // 二维码路径
            Path outputPath = Paths.get(path);
            // 写入文件
            MatrixToImageWriter.writeToPath(bitMatrix, format, outputPath);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
