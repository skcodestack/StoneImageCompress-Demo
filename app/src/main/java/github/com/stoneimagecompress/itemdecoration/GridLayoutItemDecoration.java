package github.com.stoneimagecompress.itemdecoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Email  1562363326@qq.com
 * Github https://github.com/skcodestack
 * Created by sk on 2017/5/31
 * Version  1.0
 * Description:
 */

public class GridLayoutItemDecoration extends RecyclerView.ItemDecoration {

    private Context mContext;
    private int mDrawableResId;
    private final Drawable mDivideDrawale;

    public GridLayoutItemDecoration(Context context, int drawableResId){
        this.mContext = context;
        this.mDrawableResId = drawableResId;
        mDivideDrawale = ContextCompat.getDrawable(context,drawableResId);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
//        super.onDraw(c, parent, state);

        drawHorizontal(c,parent);

        drawVertical(c,parent);

    }

    private void drawVertical(Canvas c, RecyclerView parent) {
        int count = parent.getChildCount();
        Rect rect = new Rect();
        for (int i = 0; i < count; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params= (RecyclerView.LayoutParams) child.getLayoutParams();
            rect.left = child.getRight()+params.leftMargin;
            rect.right = rect.left + mDivideDrawale.getIntrinsicWidth();
            rect.top = child.getTop() -params.topMargin;
            if(isLastRow(child,parent)){
                rect.bottom = child.getBottom();
            }else {
                rect.bottom = child.getBottom() + params.bottomMargin;
            }
            mDivideDrawale.setBounds(rect);
            mDivideDrawale.draw(c);
        }
    }

    private void drawHorizontal(Canvas c, RecyclerView parent){
        int count = parent.getChildCount();
        Rect rect = new Rect();
        for (int i = 0; i < count; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params= (RecyclerView.LayoutParams) child.getLayoutParams();

            rect.left = child.getLeft()-params.leftMargin;
            if(isLastCoulmn(child,parent)) {
                rect.right = child.getRight()+mDivideDrawale.getIntrinsicWidth();
            }else {
                rect.right = child.getRight()+mDivideDrawale.getIntrinsicWidth()+params.rightMargin;
            }

            rect.top = child.getBottom()+params.bottomMargin;
            rect.bottom = rect.top+mDivideDrawale.getIntrinsicHeight();
            mDivideDrawale.setBounds(rect);
            mDivideDrawale.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {






        if(isLastCoulmn(view,parent)){
             outRect.right=0;
        }else {
            outRect.right=mDivideDrawale.getIntrinsicWidth();
        }


        if(isLastRow(view,parent)){
            outRect.bottom = 0;
        }else {
            outRect.bottom = mDivideDrawale.getIntrinsicHeight();
        }



    }
    private boolean isLastRow(View view, RecyclerView parent){
        int postion = parent.getChildAdapterPosition(view);
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if(layoutManager instanceof GridLayoutManager){
            GridLayoutManager gridLayoutManager= (GridLayoutManager) layoutManager;
            int itemCount = gridLayoutManager.getItemCount();
            int spanCount = gridLayoutManager.getSpanCount();

            int rows = (itemCount+1) / spanCount;
            return postion > ((rows-1)*3 -1);
        }

        return false;
    }
    private boolean isLastCoulmn(View view, RecyclerView parent){
        int postion = parent.getChildAdapterPosition(view);
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if(layoutManager instanceof GridLayoutManager){
            GridLayoutManager gridLayoutManager= (GridLayoutManager) layoutManager;
            int spanCount = gridLayoutManager.getSpanCount();
            return (postion+1)%spanCount==0;
        }
        return true;
    }

}
