package github.com.stoneimagecompress.itemdecoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Email  1562363326@qq.com
 * Github https://github.com/skcodestack
 * Created by sk on 2017/5/31
 * Version  1.0
 * Description:
 */

public class LinearLayoutItemDecoration extends RecyclerView.ItemDecoration {

    private Context mContext;
    private int mDrawableResId;
    private final Drawable mDivideDrawale;

    public LinearLayoutItemDecoration(Context context,int drawableResId){
        this.mContext = context;
        this.mDrawableResId = drawableResId;
        mDivideDrawale = ContextCompat.getDrawable(context,drawableResId);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
//        super.onDraw(c, parent, state);

        int count = parent.getChildCount();
        Rect rect = new Rect();

        for (int i = 1; i < count; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            rect.left = child.getLeft();
            rect.right = child.getRight() -  parent.getPaddingRight();
            rect.bottom = child.getTop()-params.topMargin;
            rect.top = rect.bottom - mDivideDrawale.getIntrinsicHeight();

            mDivideDrawale.setBounds(rect);
            mDivideDrawale.draw(c);

        }



    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//        super.getItemOffsets(outRect, view, parent, state);
        int postion = parent.getChildAdapterPosition(view);
        if(postion > 0){
            outRect.top=mDivideDrawale.getIntrinsicHeight();
        }
    }


}
