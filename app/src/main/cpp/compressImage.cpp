#include <jni.h>
#include <string>
#include <stdlib.h>
#include "github_com_stoneimagecompress_util_ImageUtil.h"
#include <unistd.h>
#include <setjmp.h>

#include <android/bitmap.h>
#include <android/log.h>
#define LOGI(FORMAT,...) __android_log_print(ANDROID_LOG_INFO,"imagecompress",FORMAT,##__VA_ARGS__);
#define LOGE(FORMAT,...) __android_log_print(ANDROID_LOG_ERROR,"imagecompress",FORMAT,##__VA_ARGS__);
#define LOGW(FORMAT,...) __android_log_print(ANDROID_LOG_WARN,"imagecompress",FORMAT,##__VA_ARGS__);
#define LOGD(FORMAT,...) __android_log_print(ANDROID_LOG_DEBUG,"imagecompress",FORMAT,##__VA_ARGS__);

typedef u_int8_t BYTE;
struct my_error_mgr {
    struct jpeg_error_mgr pub;
    jmp_buf setjmp_buffer;
};

typedef struct my_error_mgr *my_error_ptr;

METHODDEF(void)
my_error_exit(j_common_ptr
              cinfo) {
    my_error_ptr myerr = (my_error_ptr) cinfo->err;
    (*cinfo->err->output_message)(cinfo);
    LOGW("jpeg_message_table[%d]:%s",
         myerr->pub.msg_code, myerr->pub.jpeg_message_table[myerr->pub.msg_code]);
    longjmp(myerr
                    ->setjmp_buffer, 1);
};

/**
 * 压缩的数据    宽  高  压缩质量  存放路径    是否使用哈夫曼算法完成压缩
 */
int generateJPEG(BYTE *data, int w, int h, jint quality, const char *name, boolean optimize);


int generateJPEG(BYTE *data, int w, int h, int quality, const char *name, boolean optimize) {
    int nComponent = 3;
    struct jpeg_compress_struct jcs;
    //自定义的error
    struct my_error_mgr jem;

    jcs.err = jpeg_std_error(&jem.pub);
    jem.pub.error_exit = my_error_exit;

    if (setjmp(jem.setjmp_buffer)) {
        return 0;
    }
    //为JPEG对象分配空间并初始化
    jpeg_create_compress(&jcs);
    //获取文件信息
    FILE *f = fopen(name, "wb");
    if (f == NULL) {
        return 0;
    }

    //指定压缩数据源
    jpeg_stdio_dest(&jcs, f);
    jcs.image_width = w;
    jcs.image_height = h;

    jcs.arith_code = false;
    jcs.input_components = nComponent;
    jcs.in_color_space = JCS_RGB;

    jpeg_set_defaults(&jcs);
    jcs.optimize_coding = optimize;

    //为压缩设定参数，包括图像大小，颜色空间
    jpeg_set_quality(&jcs, quality, true);
    //开始压缩
    jpeg_start_compress(&jcs, true);
    JSAMPROW row_point[1];
    int row_stride;
    row_stride = jcs.image_width * nComponent;
    while (jcs.next_scanline < jcs.image_height) {
        row_point[0] = &data[jcs.next_scanline * row_stride];
        jpeg_write_scanlines(&jcs, row_point, 1);
    }

    if (jcs.optimize_coding) {
        LOGI("使用了哈夫曼算法完成压缩");
    } else {
        LOGI("未使用哈夫曼算法");
    }
    //压缩完毕
    jpeg_finish_compress(&jcs);
    //释放资源
    jpeg_destroy_compress(&jcs);
    fclose(f);
    return 1;
}

/*
 * Class:     github_com_androidadvanced_ndk_util_ImageUtil
 * Method:    compressBitmap
 * Signature: (Ljava/lang/Object;ILjava/lang/String;B)I
 */
JNIEXPORT jint JNICALL Java_github_com_stoneimagecompress_util_ImageUtil_compressBitmap
        (JNIEnv * env, jclass clazz, jobject bitmap, jint quality, jstring dstFile,jboolean optimize){

    LOGE("%s", "===>Java_github_com_androidadvanced_1ndk_util_ImageUtil_compressBitmap");
    int ret;
    AndroidBitmapInfo bitmapInfo;
    //像素点argb
    BYTE *pixelsColor;
    //bitmap 数据
    BYTE *data;
    BYTE *tmpData;


    //获取android bitmap 信息
    if((ret = AndroidBitmap_getInfo(env,bitmap,&bitmapInfo)) < 0){
        LOGD("AndroidBitmap_getInfo() failed error=%d", ret);
        return ret;
    }

    //锁定bitmap,获取像素点argb，存储到pixelsColor中
    if((ret = AndroidBitmap_lockPixels(env,bitmap,(void**)&pixelsColor)) < 0){
        LOGD("AndroidBitmap_lockPixels() failed error=%d", ret);
        return ret;
    }

    BYTE r, g, b;
    int color;
    //获取图片信息
    int w, h, format;
    w = bitmapInfo.width;
    h = bitmapInfo.height;
    format = bitmapInfo.format;
    //只处理 RGBA_8888
    if(format != ANDROID_BITMAP_FORMAT_RGBA_8888){
        LOGD("AndroidBitmapInfo  format  is not ANDROID_BITMAP_FORMAT_RGBA_8888 error=%d", ret);
        return -1;
    }

    LOGD("bitmap: width=%d,height=%d,size=%d , format=%d ", w,h,w*h,bitmapInfo.format);

    //分配内存（存放bitmap rgb数据）
    data = (BYTE *) malloc(w * h * 3);
    //保存内存首地址
    tmpData=data;

    //将bitmap转rgb
    int i=0;
    int j=0;
    for (i = 0; i < h; ++i) {
        for (j = 0; j < w; ++j){
            //像素点
            color = *((int*) pixelsColor);
            //取argb值（各占8位）    0xffffffff--->0xaarrggbb
            r= (color >> 16) & 0xff;
            g= (color >> 8) & 0xff;
            b= (color >> 0) & 0xff;

            *data=b;
            *(data+1)=g;
            *(data+2)=r;
//            LOGI("------------------>here is changed");

            //data只存rgb
            data+=3;
            //pixelsColor中存的是argb
            pixelsColor+=4;

        }
    }

    AndroidBitmap_unlockPixels(env,bitmap);

    //进行压缩
    const char* file_path = env->GetStringUTFChars(dstFile,NULL);

    //压缩图片
    ret = generateJPEG(tmpData,w,h,quality,file_path,optimize);

    //释放内存
    free((void *) tmpData);
    env->ReleaseStringUTFChars(dstFile,file_path);

    //释放java-->bitmap
    jclass  jBitmapClass = env->GetObjectClass(bitmap);
    jmethodID jRecycleMethodId = env->GetMethodID(jBitmapClass,"recycle","()V");
    env->CallVoidMethod(bitmap,jRecycleMethodId,NULL);

    return ret;
}
