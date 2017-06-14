package github.com.stoneimagecompress.commonAdapter.helper;

/**
 * Email  1562363326@qq.com
 * Github https://github.com/skcodestack
 * Created by sk on 2017/6/5
 * Version  1.0
 * Description:
 */

public interface OnItemLongClickListener {
    /**
     * 长按事件
     * @param postion  点击的条目位置
     * @return         点击事件是否被消费（如果返回flash,则会响应点击事件）
     */
    boolean onItemLongClick(int postion);
}
