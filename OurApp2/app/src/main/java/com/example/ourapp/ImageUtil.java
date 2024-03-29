package com.example.ourapp;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtil {
    public static final int min_mosaic_block_size = 4;
    /**
     *
     * @param bitmap
     * @param targetPath
     * @param x
     * @param y
     * @param width
     * @param height
     * @param blockSize
     * @return
     * @throws OutOfMemoryError
     */
    public static void mosaic(Bitmap bitmap, String targetPath, int x, int y, int width, int height, int blockSize) throws OutOfMemoryError, IOException {
        if (bitmap == null || bitmap.getWidth() == 0 || bitmap.getHeight() == 0
                || bitmap.isRecycled()) {
            throw new RuntimeException("bad bitmap to add mosaic");
        }
        if (blockSize < min_mosaic_block_size) {
            blockSize = min_mosaic_block_size;
        }
        Rect targetRect = new Rect(x, y, width, height);
        int bw = bitmap.getWidth();
        int bh = bitmap.getHeight();
        //
        int rectW = targetRect.width();
        int rectH = targetRect.height();
        int[] bitmapPxs = new int[bw * bh];
        // fetch bitmap pxs
        bitmap.getPixels(bitmapPxs, 0, bw, 0, 0, bw, bh);
        //
        int rowCount = (int) Math.ceil((float) rectH / blockSize);
        int columnCount = (int) Math.ceil((float) rectW / blockSize);
        int maxX = bw;
        int maxY = bh;
        for (int r = 0; r < rowCount; r++) { // row loop
            for (int c = 0; c < columnCount; c++) {// column loop
                int startX = targetRect.left + c * blockSize + 1;
                int startY = targetRect.top + r * blockSize + 1;
                dimBlock(bitmapPxs, startX, startY, blockSize, maxX, maxY);
            }
        }
        File file = new File(targetPath);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        Bitmap.createBitmap(bitmapPxs, bw, bh, Bitmap.Config.ARGB_8888).compress(Bitmap.CompressFormat.JPEG,100, fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    /**
     * 从块内取样，并放大，从而达到马赛克的模糊效果
     *
     * @param pxs
     * @param startX
     * @param startY
     * @param blockSize
     * @param maxX
     * @param maxY
     */
    private static void dimBlock(int[] pxs, int startX, int startY,
                                 int blockSize, int maxX, int maxY) {
        int stopX = startX + blockSize - 1;
        int stopY = startY + blockSize - 1;
        if (stopX > maxX) {
            stopX = maxX;
        }
        if (stopY > maxY) {
            stopY = maxY;
        }
        //
        int sampleColorX = startX + blockSize / 2;
        int sampleColorY = startY + blockSize / 2;
        //
        if (sampleColorX > maxX) {
            sampleColorX = maxX;
        }
        if (sampleColorY > maxY) {
            sampleColorY = maxY;
        }
        int colorLinePosition = (sampleColorY - 1) * maxX;
        int sampleColor = pxs[colorLinePosition + sampleColorX - 1];// 像素从1开始，但是数组层0开始
        for (int y = startY; y <= stopY; y++) {
            int p = (y - 1) * maxX;
            for (int x = startX; x <= stopX; x++) {
                // 像素从1开始，但是数组层0开始
                pxs[p + x - 1] = sampleColor;
            }
        }
    }


}
