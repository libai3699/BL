package com.gp.common.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.common.core.config.RuoYiConfig;
import org.slf4j.MDC;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 图片表格生成工具
 *
 * @author Administrator
 * @date 2022/07/07
 */
public class PicTableUtil {
    public static void createImage(String fileLocation, BufferedImage image) {
        try {
//            FileOutputStream fos = new FileOutputStream(fileLocation);
//            BufferedOutputStream bos = new BufferedOutputStream(fos);
//            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(bos);
//            encoder.encode(image);
//            bos.close();
            String formatName = fileLocation.substring(fileLocation.lastIndexOf(".") + 1);
            ImageIO.write(image, formatName, new File(fileLocation));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static void saveImage(BufferedImage image, String dstName) throws IOException {
        String formatName = dstName.substring(dstName.lastIndexOf(".") + 1);
        //FileOutputStream out = new FileOutputStream(dstName);
        //JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
        //encoder.encode(dstImage);
        ImageIO.write(image, /*"GIF"*/ formatName /* format desired */ , new File(dstName) /* target */ );
    }
    public static String graphicsGeneration(ArrayList<ArrayList<String>> cellsValue, String picName) {

        //实际数据行数+标题+备注
        int totalrow = cellsValue.size() + 3;
        //列数
        int totalcol = 6;
        //图片的宽度
        int imageWidth = 1280;
        //每行的高度
        int rowheight = 40;
        //与顶部的差
        int startHeight = 10;
        //图片高度
        int imageHeight = totalrow * 40 + 20;
        //与左侧的差
        int startWidth = 5;
        //每列的宽度
        int colwidth = (int) ((imageWidth - 20) / totalcol);

        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
//        Graphics graphics = image.getGraphics();
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, imageWidth, imageHeight);
        graphics.setColor(new Color(220, 240, 240));

        //画横线
        for (int j = 0; j < totalrow; j++) {
            graphics.setColor(Color.black);
            graphics.drawLine(startWidth, startHeight + (j + 1) * rowheight, imageWidth - 5, startHeight + (j + 1) * rowheight);
        }
        //末行
        graphics.setColor(Color.black);
        graphics.drawLine(startWidth, imageHeight - 90, imageWidth - 5, imageHeight - 90);


        //画竖线
        for (int k = 0; k < totalcol; k++) {
            graphics.setColor(Color.black);
            graphics.drawLine(startWidth + k * colwidth, startHeight + rowheight, startWidth + k * colwidth, imageHeight - 50);
        }
        //末列
        graphics.setColor(Color.black);
        graphics.drawLine(imageWidth - 5, startHeight + rowheight, imageWidth - 5, imageHeight - 50);

        //设置字体
        Font font = new Font("宋体", Font.BOLD, 30);
        graphics.setFont(font);

        //写标题
        String title = "投注单";

        graphics.drawString(title, (int)(imageWidth / 2.1) + startWidth, startHeight + rowheight - 10);
        graphics.setColor(Color.darkGray);
        graphics.fillRect(startWidth + 1, startHeight +  rowheight + 1,  imageWidth - startWidth - 6 ,  rowheight -1);

        font = new Font("宋体", Font.BOLD, 23);
        graphics.setFont(font);
        graphics.setColor(Color.WHITE);
        //写入表头
        String[] headCells = {"用户名", "总投注金额", "输赢金额", "派彩金额", "投注选型","订单结果"};
        for (int m = 0; m < headCells.length; m++) {
            String headCell = headCells[m];
            int length = headCell.length();

            graphics.drawString(headCell, startWidth + colwidth * m + (int)(10*(Math.abs(length - 10))), startHeight + rowheight * 2 - 10);
        }

        graphics.setColor(Color.BLACK);
        //数据
        //设置字体
        font = new Font("宋体", Font.PLAIN, 24);
        graphics.setFont(font);
        //写入内容
        for (int n = 0; n < cellsValue.size(); n++) {
            if (n % 2 != 0) {
                graphics.setColor(new Color(186, 186, 186));
                graphics.fillRect(startWidth + 1, startHeight +  (n + 2) * rowheight + 1,  imageWidth - startWidth - 6 ,  rowheight -1);
            }else {
                graphics.setColor(Color.WHITE);
                graphics.fillRect(startWidth + 1, startHeight +  (n + 2) * rowheight + 1,  imageWidth - startWidth - 6 ,  rowheight -1);
            }
            //当是最后一行
            if (n == (cellsValue.size()-1)) {
                graphics.setColor(new Color(250, 128, 114));
                graphics.fillRect(startWidth + 1, startHeight +  (n + 2) * rowheight + 1,  imageWidth - startWidth - 6 ,  rowheight -1);
            }

            List<String> arr = cellsValue.get(n);
            for (int l = 0; l < arr.size(); l++) {
                //指定黑色画笔
                graphics.setPaint(Color.BLACK);
                String content = cellsValue.get(n).get(l);
                int length = content.length();
                if (ChineseUtil.hasChinese(content)) {
                    graphics.drawString(content, startWidth + colwidth * l + (int)(10*(Math.abs(length - 10))), startHeight + rowheight * (n + 3) - 10);
                }else {
                    graphics.drawString(content, startWidth + colwidth * l +  (int)(6*(Math.abs(length - 18))), startHeight + rowheight * (n + 3) - 10);
                }

            }
        }
        //写入内容
//        for (int n = 0; n < cellsValue.length; n++) {
//            String[] arr = cellsValue[n];
//            for (int l = 0; l < arr.length; l++) {
//                graphics.drawString(cellsValue[n][l].toString(), startWidth + colwidth * l + 5, startHeight + rowheight * (n + 3) - 10);
//            }
//        }
        //画竖线
        for (int k = 0; k < totalcol; k++) {
            graphics.setColor(Color.black);
            graphics.drawLine(startWidth + k * colwidth, startHeight + rowheight, startWidth + k * colwidth, imageHeight - 50);
        }
        //写备注
//        font = new Font("宋体", Font.BOLD, 16);
//        graphics.setFont(font);
//        graphics.setColor(Color.RED);
//        String remark = "共10人";
//        graphics.drawString(remark, startWidth, imageHeight - 30);
        String picUrl = "";
        if (StrUtil.isNotBlank(picName)) {
            if (!picName.contains(".jpg")) {
                picName = picName + ".jpg";
            }
            picUrl = getAbsoluteFile(picName);
        }else {
            picUrl = getAbsoluteFile(DateUtil.format(new Date(), "yyMMddHHmmss") + ".jpg");
        }
        createImage(picUrl, image);

        return picUrl;
    }

    public static void setPic(String picUrl){
         MDC.put("picUrl",picUrl);
    }

    public static String getPic(){
        return MDC.get("picUrl");
    }

    /**
     * 获取下载路径
     *
     * @param filename 文件名称
     */
    public static String getAbsoluteFile(String filename)
    {
        String downloadPath = RuoYiConfig.getDownloadPath() + filename;
        File desc = new File(downloadPath);
        if (!desc.getParentFile().exists())
        {
            desc.getParentFile().mkdirs();
        }
        return downloadPath;
    }



    public static void main(String[] args) {

        ArrayList<String> strings1 = CollUtil.newArrayList( "isaxingya001", "200000000", "200000", "200000000", "庄对,闲对,和", "赢" );
        ArrayList<String> strings2 = CollUtil.newArrayList( "axingya", "18.00", "20", "18.00", "庄对,闲对", "赢" );
        ArrayList<String> strings3 = CollUtil.newArrayList( "总计3人", "20", "18.00", "20", "和", "赢" );

        ArrayList<ArrayList<String>> arrayLists = CollUtil.newArrayList(strings1, strings2, strings3);
        WatchTimeUtil.doProcess(()->{
            graphicsGeneration(arrayLists, "test.jpg");
            return null;
        },"生成图片");
    }
}

