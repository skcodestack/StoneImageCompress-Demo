package github.com.stoneimagecompress.commonAdapter.helper;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

import github.com.stoneimagecompress.R;


/**
 * Email  1562363326@qq.com
 * Github https://github.com/skcodestack
 * Created by sk on 2017/6/5
 * Version  1.0
 * Description:
 */

public class GlideImageLoadHelper extends ImageLoadHelper {


    public GlideImageLoadHelper(String path) {
        super(path);
    }

    @Override
    public void loadImage(ImageView iv, String path) {
        Glide.with(iv.getContext())
                .load(path)
//                .asBitmap()
//                .asGif()
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .override(100,100)
                .error(R.drawable.miss_default_error)
                .placeholder(R.drawable.miss_default_error)
                .centerCrop()
                .into(iv);
    }
}
