package github.com.stoneimagecompress;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import github.com.stoneimagecompress.commonAdapter.RecyclerCommonAdapter;
import github.com.stoneimagecompress.commonAdapter.ViewHolder;
import github.com.stoneimagecompress.commonAdapter.helper.GlideImageLoadHelper;


/**
 * Email  1562363326@qq.com
 * Github https://github.com/skcodestack
 * Created by sk on 2017/6/7
 * Version  1.0
 * Description:
 */

public class SelectImageListAdapter extends RecyclerCommonAdapter<String> {




    List<String> mImageList;
    int mMaxCount;
    public SelectImageListAdapter(Context context, List<String> data,List<String> imageList,int maxCount) {
        super(context, data, R.layout.media_chooser_item);
        mImageList = imageList;
        mMaxCount=maxCount;
    }

    @Override
    public void bindData(final ViewHolder holder, final String itemData, int position) {
        if(TextUtils.isEmpty(itemData)){
            //拍照
            holder.setVisibility(R.id.camera_ll,View.VISIBLE);
            holder.setVisibility(R.id.media_selector_indicator,View.INVISIBLE);
            holder.setVisibility(R.id.image,View.INVISIBLE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       //不能大于最大张数
                       if(mImageList.size() >= mMaxCount){
                           Toast.makeText(mContext,"最大张数不超过"+mMaxCount+"张",Toast.LENGTH_SHORT).show();
                           return;
                       }

                        if(mCaptureImageListener != null){
                            mCaptureImageListener.captureImage();
                        }
                   }
            });

        }else {
            //图片
            holder.setVisibility(R.id.camera_ll,View.INVISIBLE);
            holder.setVisibility(R.id.media_selector_indicator,View.VISIBLE);
            holder.setVisibility(R.id.image,View.VISIBLE);

            holder.setImagePath(R.id.image,new GlideImageLoadHelper(itemData));


            final ImageView  selectorIndicator = holder.getView(R.id.media_selector_indicator);
            if(mImageList.contains(itemData)){
                selectorIndicator.setSelected(true);
                holder.setVisibility(R.id.mask,View.VISIBLE);

            }else {
                selectorIndicator.setSelected(false);
                holder.setVisibility(R.id.mask,View.GONE);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(mImageList.contains(itemData)){
                        mImageList.remove(itemData);
                        selectorIndicator.setSelected(false);
                        holder.setVisibility(R.id.mask,View.GONE);
                    }else {
                        //不能大于最大张数
                        if(mImageList.size() >= mMaxCount){
                            Toast.makeText(mContext,"最大张数不超过"+mMaxCount+"张",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        mImageList.add(itemData);
                        selectorIndicator.setSelected(true);
                        holder.setVisibility(R.id.mask,View.VISIBLE);
                    }

                    if(mSelectListener !=null){
                        mSelectListener.selectedChangedListener(itemData);
                    }
                }
            });

        }
    }

    OnCaptureImageListener mCaptureImageListener;

    public void setOnCaptureImageListener(OnCaptureImageListener listener){
        this.mCaptureImageListener = listener;
    }

    public interface OnCaptureImageListener{
        void captureImage();
    }


    OnItemSelectedChangedListener mSelectListener;

    public void setOnItemSelectListener(OnItemSelectedChangedListener listener){
        this.mSelectListener = listener;
    }
}
