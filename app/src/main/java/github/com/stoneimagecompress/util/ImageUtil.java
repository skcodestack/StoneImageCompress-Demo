package github.com.stoneimagecompress.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Email  1562363326@qq.com
 * Github https://github.com/skcodestack
 * Created by sk on 2017/6/7
 * Version  1.0
 * Description:
 */

public class ImageUtil {

    static {
        System.loadLibrary("compressImage");
    }

    /**
     *Android中常用压缩方法分为2种：一种是降采样率压缩，另外一种是质量压缩。
     * 第一种
     * BitmapFactory.Options o = new BitmapFactory.Options();
     * o.inJustDecodeBounds = true;
     * BitmapFactory.decodeFile(path, o);
     * o.inSampleSize=自己计算
     * o.inJustDecodeBounds = false;
     * BitmapFactory.decodeFile(path, o);
     *
     *
     *第二种
     * bitmap.compress(Bitmap.CompressFormat.JPEG, 20, new FileOutputStream("sdcard/result.jpg"));
     *
     */

    /**
     * 使用libjpeg进行压缩
     * 压缩前最好进行降采样率压缩先进行一次压缩，来固定图片宽度
     * @param bitmap   压缩的图片
     * @param quality   质量
     * @param dstFile   新的图片路径
     * @param optimize  是否使用哈夫曼算法完成压缩（使用哈夫曼算法压缩，压缩率高10~25倍）
     * @return   是否压缩成功
     */
    public static boolean compressImage(Bitmap bitmap,int quality,String dstFile,boolean  optimize){

        int ret = compressBitmap( bitmap, quality, dstFile,  optimize);

        return  ret==1;
    }

    public static native int compressBitmap(Bitmap bitmap, int quality, String dstFile,boolean  optimize);

    /**
     *
     * @param path
     * @return
     */
    public static Bitmap decodeFile(String path){

        int finalWidth = 960;
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, o);
        o.inSampleSize = calculateSize(o.outWidth,finalWidth);
        o.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, o);
    }

    private static int calculateSize(int originalWidth,int newWidth) {
        return originalWidth/newWidth;
    }
}
