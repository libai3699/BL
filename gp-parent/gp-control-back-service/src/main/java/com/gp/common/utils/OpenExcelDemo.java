package com.gp.common.utils;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

public class OpenExcelDemo {

    public static void main(String[] args) throws Exception {
        WatchTimeUtil.doProcess(()->{
            try {
                doData();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        },"生成图片");

    }

    private static void doData() throws Exception {
        openExcel("E:\\excel\\下注-期号-220706233100.xlsx");

        copyRectByPix(37, 207, 1215, 665); //给定两个坐标点的数据，圈定截图范围

        createImageFileFromClip("E:\\excel\\" + getTimeStr() + ".jpg");

        closeExcel((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 15, 12);    //需要模拟关闭事件，将Excel关闭，以便下次能正确打开报表
    }

    private static void closeExcel(int i, int j) throws Exception {
        Robot robot = new Robot();

        robot.delay(500);
        robot.mouseMove(i,j);
        robot.delay(500);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.delay(500);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        robot.delay(500);
        robot.keyPress(KeyEvent.VK_ENTER);
    }

    public static void openExcel(String dir) throws Exception {
        Runtime.getRuntime().exec("cmd /k " + dir + "");
    }

    public static void copyRectByPix(int fromX, int fromY, int toX, int toY)
            throws Exception {
        Robot robot = new Robot();
        robot.delay(3000); // 延时3000毫秒

        robot.mouseMove(fromX, fromY);
        robot.delay(500);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.delay(500);
        robot.mouseMove(toX, toY);
        robot.delay(500);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);

        robot.setAutoDelay(200);
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_C);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_C);
    }

    public static void createImageFileFromClip(String dir) throws Exception {
        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard()
                .getContents(null);
        if (null != t && t.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            Image image = (Image) t.getTransferData(DataFlavor.imageFlavor);
            savePic(image, dir);
        }
    }

    public static String savePic(Image iamge, String dir) throws Exception {
        int w = iamge.getWidth(null);
        int h = iamge.getHeight(null);

        // 首先创建一个BufferedImage变量，因为ImageIO写图片用到了BufferedImage变量。
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);

        // 再创建一个Graphics变量，用来画出来要保持的图片，及上面传递过来的Image变量
        Graphics g = bi.getGraphics();

        g.drawImage(iamge, 0, 0, null);

        // 将BufferedImage变量写入文件中。
        ImageIO.write(bi, "jpg", new File(dir));
        return dir;
    }

    public static String getTimeStr() {
        String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return time;
    }
}
