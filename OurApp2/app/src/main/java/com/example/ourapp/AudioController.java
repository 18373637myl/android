package com.example.ourapp;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.FrameRecorder.Exception;

import java.io.File;

public class AudioController {
    /**
     * 通用音频格式参数转换
     *
     * @param inputFile
     *            -导入音频文件
     * @param outputFile
     *            -导出音频文件
     * @param audioCodec
     *            -音频编码
     * @param sampleRate
     *            -音频采样率
     * @param audioBitrate
     *            -音频比特率
     */
    public static void convert(String inputFile, String outputFile, int audioCodec, int sampleRate, int audioBitrate,
                               int audioChannels) {
        Frame audioSamples = null;
        // 音频录制（输出地址，音频通道）
        FFmpegFrameRecorder recorder = null;
        //抓取器
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);

        // 开启抓取器
        if (start(grabber)) {
            recorder = new FFmpegFrameRecorder(outputFile, audioChannels);
            recorder.setAudioOption("crf", "0");
            recorder.setAudioCodec(audioCodec);
            recorder.setAudioBitrate(audioBitrate);
            recorder.setAudioChannels(audioChannels);
            recorder.setSampleRate(sampleRate);
            recorder.setAudioQuality(0);
            recorder.setAudioOption("aq", "10");
            // 开启录制器
            if (start(recorder)) {
                try {
                    // 抓取音频
                    while ((audioSamples = grabber.grab()) != null) {
                        recorder.setTimestamp(grabber.getTimestamp());
                        recorder.record(audioSamples);
                    }

                } catch (FrameGrabber.Exception e1) {
                    System.err.println("抓取失败");
                } catch (Exception e) {
                    System.err.println("录制失败");
                }
                stop(grabber);
                stop(recorder);
            }
        }
    }

    public static void mergeAudioAndVideo(String inputImage, String inputAudio, String outputVideo) throws FrameGrabber.Exception, Exception {

        String imageInput = inputImage;
        FFmpegFrameGrabber imageGrabber = new FFmpegFrameGrabber(imageInput);
        start(imageGrabber);
        String audioInput = inputAudio;
        FFmpegFrameGrabber audioGrabber = new FFmpegFrameGrabber(audioInput);
        start(audioGrabber);
        String outputPath = outputVideo;
        // 流媒体输出地址，分辨率（长，高），是否录制音频（0:单声道/1:立体声）
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputPath, imageGrabber.getImageWidth(), imageGrabber.getImageHeight(), 1);
        recorder.setInterleaved(true);
        recorder.setVideoCodec(28);
        recorder.setFormat("mp4");
        recorder.setPixelFormat(0); // yuv420p
        int frameRate = (int)imageGrabber.getFrameRate();
        recorder.setFrameRate(frameRate);
        recorder.setGopSize(frameRate * 2);
        recorder.setAudioOption("crf", "0");
        recorder.setAudioQuality(0);// 最高质量
        recorder.setAudioCodec(86018);
        recorder.setAudioChannels(2);
        recorder.setSampleRate(audioGrabber.getSampleRate());
        start(recorder);
        long videoTime = imageGrabber.getLengthInTime();
        Frame imageFrame = null;
        while ((imageFrame = imageGrabber.grabImage()) != null) {
            recorder.record(imageFrame);
        }
        Frame sampleFrame = null;
        while ((sampleFrame = audioGrabber.grabSamples()) != null) {
            recorder.record(sampleFrame);
            if (audioGrabber.getTimestamp() >= videoTime) {
                break;
            }
        }
        stop(recorder);
        stop(audioGrabber);
        stop(imageGrabber);

    }

    public static boolean start(FrameGrabber grabber) {
        try {
            grabber.start();
            return true;
        } catch (FrameGrabber.Exception e2) {
            try {
                System.err.println("首次打开抓取器失败，准备重启抓取器...");
                grabber.restart();
                return true;
            } catch (FrameGrabber.Exception e) {
                try {
                    System.err.println("重启抓取器失败，正在关闭抓取器...");
                    grabber.stop();
                } catch (FrameGrabber.Exception e1) {
                    System.err.println("停止抓取器失败！");
                }
            }

        }
        return false;
    }

    public static boolean start(FrameRecorder recorder) {
        try {
            recorder.start();
            return true;
        } catch (Exception e2) {
            try {
                System.err.println("首次打开录制器失败！准备重启录制器...");
                recorder.stop();
                recorder.start();
                return true;
            } catch (Exception e) {
                try {
                    System.err.println("重启录制器失败！正在停止录制器...");
                    recorder.stop();
                } catch (Exception e1) {
                    System.err.println("关闭录制器失败！");
                }
            }
        }
        return false;
    }

    public static boolean stop(FrameGrabber grabber) {
        try {
            grabber.flush();
            grabber.stop();
            return true;
        } catch (FrameGrabber.Exception e) {
            return false;
        } finally {
            try {
                grabber.stop();
            } catch (FrameGrabber.Exception e) {
                System.err.println("关闭抓取器失败");
            }
        }
    }

    public static boolean stop(FrameRecorder recorder) {
        try {
            recorder.stop();
            recorder.release();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            try {
                recorder.stop();
            } catch (Exception e) {

            }
        }
    }
    public static void main(String[] args) throws Exception, FrameGrabber.Exception {
        String inputfile = "C:\\Users\\LYYRE-OAO\\Desktop\\testvideo.flv";
        String inputaudio = "C:\\Users\\LYYRE-OAO\\Desktop\\AOT_S1E1_Trim.mp4";
        String outputfile = "C:\\Users\\LYYRE-OAO\\Desktop\\mergevideo.flv";
//        convert(inputfile, outputfile, avcodec.AV_CODEC_ID_MP3, 8000, 16, 2);
//        mergeAudioAndVideo(inputfile, inputaudio, outputfile);
//        mergeAudioAndVideo();
    }
} 