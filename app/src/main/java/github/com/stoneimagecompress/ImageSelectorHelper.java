package github.com.stoneimagecompress;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

/**
 * Email  1562363326@qq.com
 * Github https://github.com/skcodestack
 * Created by sk on 2017/6/8
 * Version  1.0
 * Description:
 */

public class ImageSelectorHelper {

    //是单选还是多选
    private int  mMode = ImageSelectActivity.MODE_MULTI;
    //最大选择张数
    private int mMaxCount = 8;
    //已经选中的图片
    private ArrayList<String> mResultList;
    //是否可以拍照
    private boolean mShowCamera = true;


    private static ImageSelectorHelper mHelper =new ImageSelectorHelper();
    private ImageSelectorHelper(){

    }

    public static ImageSelectorHelper  create(){
        return mHelper;
    }

    public ImageSelectorHelper multi(){
        mMode = ImageSelectActivity.MODE_MULTI;
        return this;
    }

    public ImageSelectorHelper signal(){
        mMode = ImageSelectActivity.MODE_SIGNAL;
        return this;
    }

    public ImageSelectorHelper count( int maxCount){
        mMaxCount=maxCount;
        return this;
    }
    public ImageSelectorHelper origin(ArrayList<String> data){
        mResultList = data;
        return this;
    }

    public ImageSelectorHelper showCamera( boolean showCamera){
        mShowCamera = showCamera;

        return this;
    }


    public void start(Activity activity,int requestcode){
        Intent intent = new Intent(activity,ImageSelectActivity.class);
        addParam(intent);
        activity.startActivityForResult(intent,requestcode);
    }
    public void start(Fragment fragment, int requestcode){
        start(fragment.getActivity(),requestcode);
    }
    private void addParam(Intent intent) {
        intent.putExtra(ImageSelectActivity.EXTRA_SELECT_COUNT,mMaxCount);
        intent.putExtra(ImageSelectActivity.EXTRA_SELECT_MODE,mMode);
        intent.putExtra(ImageSelectActivity.EXTRA_SHOW_CAMERA,mShowCamera);
        intent.putStringArrayListExtra(ImageSelectActivity.EXTRA_DEFAULT_SELECTED_LIST,mResultList);
    }


}
