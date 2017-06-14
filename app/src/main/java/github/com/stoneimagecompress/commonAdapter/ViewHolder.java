package github.com.stoneimagecompress.commonAdapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import github.com.stoneimagecompress.commonAdapter.helper.ImageLoadHelper;


/**
 * Email  1562363326@qq.com
 * Github https://github.com/skcodestack
 * Created by sk on 2017/6/5
 * Version  1.0
 * Description:
 */

public class ViewHolder extends RecyclerView.ViewHolder {
    //对view进行缓存（弱引用）
    SparseArray<WeakReference<View>> mCache ;

    public ViewHolder( View itemView) {
        super(itemView);
        mCache = new SparseArray<>();
    }

    public ViewHolder setText(int viewId, String content){
        TextView view = getView(viewId);
        view.setText(content);
        return this;
    }

    public ViewHolder setImageResource(int viewId,int resId){
        ImageView iv=getView(viewId);
        iv.setImageResource(resId);
        return this;
    }

    public ViewHolder setImagePath(int viewId,ImageLoadHelper imageLoadHelper){
        ImageView iv=getView(viewId);

        if(imageLoadHelper!=null){
            imageLoadHelper.loadImage(iv,imageLoadHelper.getPath());
        }

        return this;
    }

    public ViewHolder setVisibility(int viewId,int visiable){
        View view =getView(viewId);
        view.setVisibility(visiable);
        return this;
    }


    public <T extends View> T getView(int viewId){
        View view = null;
        if(mCache == null){
            mCache = new SparseArray<>();
        }
        WeakReference<View> weakReference = mCache.get(viewId);
        if(weakReference != null){
            view = weakReference.get();
        }
        if(view == null){
            view =itemView.findViewById(viewId);
            mCache.put(viewId,new WeakReference<View>(view));
        }


        return (T) view;
    }




}
