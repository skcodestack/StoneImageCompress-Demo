package github.com.stoneimagecompress;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import github.com.stoneimagecompress.util.ImageUtil;
import github.com.stoneimagecompress.util.ThreadPoolManager;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> imageList = new ArrayList<>();
    private static final int SELECT_IMAGE_REQUEST = 0X00011;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void choose(View view){
        //        ImageUtil.compressImage(null,10,"dfd",true);
//        Intent intent=new Intent(this,ImageSelectActivity.class);
//        intent.putExtra(ImageSelectActivity.EXTRA_SELECT_COUNT,10);
//        intent.putExtra(ImageSelectActivity.EXTRA_SELECT_MODE,ImageSelectActivity.MODE_MULTI);
//        intent.putExtra(ImageSelectActivity.EXTRA_SHOW_CAMERA,true);
//        intent.putStringArrayListExtra(ImageSelectActivity.EXTRA_DEFAULT_SELECTED_LIST,imageList);
//        startActivity(intent);

        ImageSelectorHelper.create().count(10).multi().origin(imageList).showCamera(true).start(this,SELECT_IMAGE_REQUEST);


    }
    //为并发做准备（线程安全）
    final CopyOnWriteArrayList<String> compressImageList=new CopyOnWriteArrayList<>();

    public void compress(View view){


        File compressDir =new File(Environment.getExternalStorageDirectory(),"compress_tem");
        if(!compressDir.exists()){
            compressDir.mkdirs();
        }
        String temDir = compressDir.getAbsolutePath();
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



    }

    public void showinfo(View view){
        Log.e("MainAcitivyt","别压缩图片信息=====>"+compressImageList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == SELECT_IMAGE_REQUEST){
                imageList = data.getStringArrayListExtra(ImageSelectActivity.EXTRA_RESULT);



            }
        }

    }
}
