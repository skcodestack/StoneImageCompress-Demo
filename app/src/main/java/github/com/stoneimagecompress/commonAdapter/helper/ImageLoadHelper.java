package github.com.stoneimagecompress.commonAdapter.helper;

import android.widget.ImageView;

/**
 * Email  1562363326@qq.com
 * Github https://github.com/skcodestack
 * Created by sk on 2017/6/5
 * Version  1.0
 * Description:
 */

public abstract class ImageLoadHelper {

     protected String path;
     public ImageLoadHelper(String path){
          this.path=path;
     }
     public abstract void loadImage(ImageView iv, String path);

     public String getPath(){
          return path;
     }
}
