package github.com.stoneimagecompress.commonAdapter.helper;

/**
 * Email  1562363326@qq.com
 * Github https://github.com/skcodestack
 * Created by sk on 2017/6/5
 * Version  1.0
 * Description: 多个条目帮助类
 */

public interface MulitiTypeSupport<T> {
    /**
     * 返回不同的布局
     * @param item
     * @return
     */
    int getLayoutId(T item);
}
