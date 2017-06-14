package github.com.stoneimagecompress.commonAdapter.helper;

import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.Collections;
import java.util.List;

/**
 * Email  1562363326@qq.com
 * Github https://github.com/skcodestack
 * Created by sk on 2017/6/6
 * Version  1.0
 * Description:
 */

public class DragItemTouchHelper extends ItemTouchHelper.Callback{

    List<? extends Object> mData=null;
    RecyclerView.Adapter mAdapter;
    int mSelectedColor = Color.GRAY;
    int mNormalColor = Color.WHITE;


    public DragItemTouchHelper(List<?> list, RecyclerView.Adapter adapter,int selectedColor,int normalColor){
        mData=list;
        mAdapter = adapter;
        mSelectedColor = selectedColor;
        mNormalColor = normalColor;
    }
    public DragItemTouchHelper(List<?> list, RecyclerView.Adapter adapter){
        mData=list;
        mAdapter = adapter;
    }
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //ACTION_STATE_IDLE ACTION_STATE_SWIPE  ACTION_STATE_DRAG
        int dragFlags = 0;
        //LEFT UP  RIGHT DOWN
        int swipeFlags = ItemTouchHelper.LEFT;

        if(recyclerView.getLayoutManager() instanceof GridLayoutManager){
            dragFlags = ItemTouchHelper.LEFT|ItemTouchHelper.UP|ItemTouchHelper.RIGHT|ItemTouchHelper.DOWN;

        }else {
            dragFlags = ItemTouchHelper.UP|ItemTouchHelper.DOWN;
        }

        return makeMovementFlags(dragFlags,swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int fromPostion = viewHolder.getAdapterPosition();
        int targetPostion = target.getAdapterPosition();
        mAdapter.notifyItemMoved(fromPostion,targetPostion);
        return true;
    }

    @Override
    public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);


        Collections.swap(mData,fromPos,toPos);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        //状态改变
        if(actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            viewHolder.itemView.setBackgroundColor(mSelectedColor);
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setBackgroundColor(mNormalColor);

        ViewCompat.setTranslationX(viewHolder.itemView,0);
    }
}
