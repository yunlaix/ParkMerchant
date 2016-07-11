package com.xs.parkmerchant.View;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by ml on 2016/7/7.
 */
public class QRCoderView {

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    /**
     * 将字符串按照指定大小生成二维码图片
     */
    public static Bitmap encodeToQR(String contentsToEncode, Bitmap logo, int dimension) throws Exception{
        if(TextUtils.isEmpty(contentsToEncode))
            return null;

        BarcodeFormat format = BarcodeFormat.QR_CODE;
        Map hints = new EnumMap(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        //容错级别
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

//        BitMatrix result = new MultiFormatWriter().encode(contentsToEncode, format, dimension, dimension, hints);
        BitMatrix result = new QRCodeWriter2().encode(contentsToEncode,format, dimension, dimension, hints);
        int width = result.getWidth();
        int height = result.getHeight();

        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;

            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        if(logo!=null){
            bitmap = addLogo(bitmap, logo);
        }

        return bitmap;
    }




//    public static Bitmap encodeToQRWidth(String contentsToEncode, Bitmap logo, int dimension) throws Exception{
//        if(TextUtils.isEmpty(contentsToEncode))
//            return null;
//
//        BarcodeFormat format = BarcodeFormat.QR_CODE;
//        Map hints = new EnumMap(EncodeHintType.class);
//        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
//        BitMatrix result = new MultiFormatWriter().encode(contentsToEncode, format, dimension, dimension, hints);
//        int width = result.getWidth();
//        int height = result.getHeight();
//
//        boolean isFirstBlack = true;
//        int startX = 0;
//        int startY = 0;
//
//        int[] pixels = new int[width * height];
//        for (int y = 0; y < height; y++) {
//            int offset = y * width;
//            for (int x = 0; x < width; x++) {
//                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
//                if(result.get(x, y) && isFirstBlack){
//                    isFirstBlack = false;
//                    startX = x;
//                    startY = y;
//                }
//            }
//        }
//        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//
//        Matrix m = new Matrix();
//        float sx = (width + 2f*startX) / width;
//        float sy = (height + 2f*startY) / height;
//        m.postScale(sx, sy);
//
//        Bitmap qrBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(qrBitmap);
//        canvas.translate(-startX, -startY);
//
//        Paint paint = new Paint();
//        paint.setAntiAlias(true);
//
//        canvas.drawBitmap(bitmap, m, paint);
//        canvas.save();
//
//        if(logo!=null){
//            bitmap = addLogo(bitmap, logo);
//        }
//
//        return qrBitmap;
//    }

    /**
     * 在二维码中间添加Logo图案
     */
    private static Bitmap addLogo(Bitmap src, Bitmap logo) {
        if (src == null) {
            return null;
        }

        if (logo == null) {
            return src;
        }

        //获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }

        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }

        //logo大小为二维码整体大小的1/5
        float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);

            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }

        return bitmap;
    }

}
