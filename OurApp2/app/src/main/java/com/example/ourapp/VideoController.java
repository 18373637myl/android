package com.example.ourapp;

import android.graphics.Bitmap;

import org.bytedeco.javacv.*;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.Frame;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;

public class VideoController {

    static int width = 0;
    static int height = 0;
    static int framelen = 0;
    static int framerate = 0;
    static ArrayList<Bitmap> bmplist = new ArrayList<>(); // 视频转成帧后的图片列表
    static File[] flist; //
    static ArrayList<HashMap<String, Integer>> area = new ArrayList<>(); // 每一张图片需要打码位置
    static String picPath = "C:\\Users\\LYYRE-OAO\\Desktop\\test\\"; // 提取得每帧图片存放位置
    static String afterpicPath = "C:\\Users\\LYYRE-OAO\\Desktop\\afterpic\\"; // 处理后每帧图片存放位置
    static String videoPath = "C:\\Users\\LYYRE-OAO\\Desktop\\AOT_S1E1_Trim.mp4";  // 原视频文件路径
    static String outputPath = "C:\\Users\\LYYRE-OAO\\Desktop\\testvideo.mp4"; // 未添加音频视频文件路径
    static String outputVideo = "C:\\Users\\LYYRE-OAO\\Desktop\\mergevideo.mp4"; // 添加了音频的最终导出视频路径
    // 主函数
    public static void main(String[] args){
        try {
            fetchPic();
            for(int i = 0; i < flist.length; i++){
                addArea(200, 400, 500, 300);
            }
            merge();
            mergeAudioAndVideo();
            release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化
     * @param videopath
     * @param afterpicpath
     * @param outputpath
     * @param outputvideo
     */
    public static void init(String videopath, String afterpicpath, String outputpath, String outputvideo){
        videoPath = videopath;
        afterpicPath = afterpicpath;
        outputPath = outputpath;
        outputVideo = outputvideo;
    }

    /**
     * 清理中间过程文件
     */
    public static void release(){
        File tfile = new File(outputPath);
        tfile.delete();
        tfile = new File(picPath);
        File[] tflist = tfile.listFiles();
        for(File file : tflist){
            file.delete();
        }
        tfile = new File(afterpicPath);
        tflist = tfile.listFiles();
        for(File file : tflist){
            file.delete();
        }
    }

    /**
     * 指定每一张图片的打码区域
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public static void addArea(int x, int y, int width, int height){
        HashMap<String, Integer> hashMap = new HashMap<>();
        hashMap.put("x", x);
        hashMap.put("y", y);
        hashMap.put("width", width);
        hashMap.put("height", height);
        area.add(hashMap);
    }

    /**
     * 将视频转成帧
     * @throws Exception
     */
    public static void fetchPic() throws Exception{
        fetchPic(new File(videoPath), picPath, 0);
    }

    /**
     * 对图片打码
     */
    public static void merge() throws Exception{
        merge(outputPath, area);
    }

    /**
     * 给视频添加音频
     * @throws Exception
     */
    public static void mergeAudioAndVideo() throws Exception {
        AudioController.mergeAudioAndVideo(outputPath, videoPath, outputVideo);
    }

    /**
     * 获取指定视频的帧并保存为图片至指定目录
     * @param file  源视频文件
     * @param picPath  截取帧的图片存放路径
     * @throws Exception
     */
    public static void fetchPic(File file, String picPath,int second) throws Exception{

        FFmpegFrameGrabber ff = new FFmpegFrameGrabber(file); // 获取视频文件
        System.out.println(VideoController.getVideoTime(file)); // 显示视频长度（秒/s）

        ff.start(); // 调用视频文件播放
        framelen = ff.getLengthInVideoFrames(); //视频帧数长度
        framerate = (int)(ff.getFrameRate());
//        framerate = 24;
        System.out.println(ff.getFrameRate());


        int i = 0; // 图片帧数，如需跳过前几秒，则在下方过滤即可
        Frame frame = null;
        int count = 0;
        while (i < framelen) {
            frame = ff.grabImage(); // 获取该帧图片流
            System.out.print(i + ",");
            if(frame!=null && frame.image!=null) {
                System.out.println(i);
                writeToBitmap(frame, count, second); // 生成帧图片
                count++;
            }
            i++;
        }

        ff.stop();
    }

    /**
     *
     * @param frame // 视频文件对象
     * @param count // 当前取到第几帧
     * @param second // 每隔多少帧取一张，一般高清视频每秒 20-24 帧，根据情况配置，如果全部提取，则将second设为 0 即可
     */
    public static void writeToBitmap(Frame frame, int count, int second) throws FrameGrabber.Exception {
        if (second == 0) {
            // 跳过间隔取帧判断
        } else if (count % second != 0){ // 提取倍数，如每秒取一张，则： second = 20
            return;
        }
        AndroidFrameConverter converter = new AndroidFrameConverter();
        Bitmap originalBitmap = converter.convert(frame);
        bmplist.add(originalBitmap);
    }

    /**
     * 获取视频时长，单位为秒
     * @param file
     * @return 时长（s）
     */
    public static Long getVideoTime(File file){
        Long times = 0L;
        try {
            FFmpegFrameGrabber ff = new FFmpegFrameGrabber(file);
            ff.start();
            times = ff.getLengthInTime()/(1000*1000);
            ff.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return times;
    }

    /**
     * 对获得图片进行马赛克处理
     * @param saveMp4name
     * @param area
     * @throws Exception
     */
    public static void merge(String saveMp4name, ArrayList<HashMap<String, Integer>> area) throws Exception  {
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(saveMp4name, width, height);
		recorder.setVideoCodec(28); // 28
//        recorder.setVideoCodec(avcodec.AV_CODEC_ID_FLV1); // 28
//		recorder.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4); // 13
        recorder.setFormat("mp4");
        //	recorder.setFormat("flv,mov,mp4,m4a,3gp,3g2,mj2,h264,ogg,MPEG4");
        recorder.setFrameRate(framerate);
        recorder.setPixelFormat(0); // yuv420p
        recorder.start();
        //
        OpenCVFrameConverter.ToIplImage conveter = new OpenCVFrameConverter.ToIplImage();
        // 列出目录中所有的图片，都是jpg的，以1.jpg,2.jpg的方式，方便操作
        for(int i = 0; i < bmplist.size(); i++){
            Bitmap bitmap = bmplist.get(i);
            String afterframe = afterpicPath + i +".jpg";
            try {
                ImageUtil.mosaic(bitmap, afterframe, area.get(i).get("x"), area.get(i).get("y"), area.get(i).get("width"), area.get(i).get("height"), 10);
                System.out.println("输出了打码之后的文件："+i+".jpg");
            } catch (Exception e) {
                System.err.println("出错了！！！！");
                e.printStackTrace();
            }
        }
        // 循环所有图片
        for(int i = 0; i < flist.length; i++ ){
            String fname = afterpicPath + i + ".jpg";
            IplImage image = cvLoadImage(fname); // 非常吃内存！！
            recorder.record(conveter.convert(image));
            // 释放内存？ cvLoadImage(fname); // 非常吃内存！！
            opencv_core.cvReleaseImage(image);
        }
        recorder.stop();
        recorder.release();
    }
}
