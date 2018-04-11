# StoneImageCompress-Demo
## Android使用JPEG实现图片压缩上传


## 1.介绍

Android中常用压缩方法分为2种：一种是降采样率压缩，另外一种是质量压缩。

第一种:

     BitmapFactory.Options o = new BitmapFactory.Options();
     o.inJustDecodeBounds = true;
     BitmapFactory.decodeFile(path, o);
     o.inSampleSize=自己计算
     o.inJustDecodeBounds = false;
     BitmapFactory.decodeFile(path, o);
     
     
第二种:

	bitmap.compress(Bitmap.CompressFormat.JPEG, 20, new FileOutputStream("sdcard/result.jpg"));
     

相信大家都用过，但是压缩比例很小，如果压缩的太多，就会导致图片失真，但是我发发现IOS系统上的图片只有100k,200k左右却很清晰，它们用的什么方式来压缩的呢？

今天我们就来使用jpeg的方式来进行对图片压缩：

## 2.编码前准备工作


ndk工具包下载可以到http://www.androiddevtools.cn/ 下载解压就行了

libjpeg库源码

		git clone git://git.linaro.org/people/tomgall/libjpeg-turbo/libjpeg-turbo.git -b linaro-android  

用ndk命令进行编译

		ndk-build APP_ABI=armeabi-v7a,armeabi 


## 3.编写代码

### 3.1 把动态库和头文件添加到我们项目中

<img src="/photo/imagecompress01.png" width="32%" />

### 3.2编写java层代码

	public class ImageUtil {

    static {
        System.loadLibrary("compressImage");
    }
    
    /**
     * 使用libjpeg进行压缩
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


### 3.3生成头文件

	javah -classpath . -jni github.com.androidadvanced_ndk.util.ImageUtil

### 3.4 编写cmake和配置gradle

#### cmake:

		

		cmake_minimum_required(VERSION 3.4.1)
		
		set(distribution_DIR ../../../../libs )
		set(SOURCE_FILES src/main/cpp/compressImage.cpp)
		set(INC_DIR src/main/cpp/include)
		
		include_directories(src/main/cpp/include)
		
		find_library(   log-lib
		                log )
		find_library(graphics jnigraphics)
		
		add_library(    libjpeg
		                SHARED
		                 IMPORTED )
		
		
		set_target_properties(  libjpeg
		                        PROPERTIES IMPORTED_LOCATION
		                        ${distribution_DIR}/${ANDROID_ABI}/libjpeg.so)
		
		
		set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")
		
		
		
		
		add_library(    compressImage
		                SHARED
		                ${SOURCE_FILES} )
		
		
		
		target_link_libraries(  compressImage
		                        libjpeg
		                        ${log-lib}
		                        ${graphics})



#### build.gradle

		ndk{
            abiFilters "armeabi-v7a" ,"armeabi"
        }

		sourceSets {
        main {
            jniLibs.srcDirs = ['libs']
        }
    	}	


### 3.5编写c代码

		

		#include <jni.h>
		#include <string>
		#include <stdlib.h>
		#include "github_com_androidadvanced_ndk_util_ImageUtil.h"
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
		JNIEXPORT jint JNICALL Java_github_com_androidadvanced_1ndk_util_ImageUtil_compressBitmap
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



每行都有注释，相信大家一看就明白了。这边不多做解释了。



### 3.6使用

		//线程安全
    	CopyOnWriteArrayList<String> compressImageList=new CopyOnWriteArrayList<>();


		//开线程池
        ThreadPoolManager.ThreadPool threadPool = ThreadPoolManager.getInstance().getShortTreadPool();

        for (final String imagePath : imageList) {
            final String temFilePath = temDir + File.separator + new File(imagePath).getName();

            threadPool.excute(new Runnable() {
                @Override
                public void run() {

                    Bitmap bitmap = ImageUtil.decodeFile(imagePath);
                    if(ImageUtil.compressImage(bitmap,65,temFilePath,true)){
                        compressImageList.add(temFilePath);
                    }
                    if(bitmap != null) {
                        bitmap.recycle();
                    }
                }
            });
        }


这边就多了一个线程池，其实也没什么东西，简单的调用。



下面我们来看下效果

### <font color=red>压缩前</font>

<img src="/photo/imagecompress02.jpg" width="32%" />


### <font color=red>压缩后</font>
<img src="/photo/imagecompress01.jpg" width="32%" />



我们对比发现，压缩了20几倍，那么图片的清晰度呢？有没有改变，或者说改变的大不大，又没有失真？


<img src="/photo/imagecompress03.jpg" width="32%" />

<img src="/photo/imagecompress04.jpg" width="32%" />
	

不知道你们能不能看出区别，反正我没发现有多大改变。

