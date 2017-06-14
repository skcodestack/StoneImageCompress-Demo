package github.com.stoneimagecompress.commonAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import github.com.stoneimagecompress.commonAdapter.helper.MulitiTypeSupport;
import github.com.stoneimagecompress.commonAdapter.helper.OnItemClickListener;
import github.com.stoneimagecompress.commonAdapter.helper.OnItemLongClickListener;


/**
 * Email  1562363326@qq.com
 * Github https://github.com/skcodestack
 * Created by sk on 2017/6/2
 * Version  1.0
 * Description:
 */

public abstract class RecyclerCommonAdapter<Data>  extends RecyclerView.Adapter<ViewHolder>{


    private  LayoutInflater mLayoutInflater;
    protected List<Data> mData;
    private int mLayoutId;
    protected Context mContext;

    private MulitiTypeSupport mMulitiTypeSupport;

    public RecyclerCommonAdapter (Context context, List<Data> data,MulitiTypeSupport mulitiTypeSupport){
        this(context,data,0);
        this.mMulitiTypeSupport=mulitiTypeSupport;
    }

    public RecyclerCommonAdapter(Context context, List<Data> data, int layoutId){
        mLayoutInflater = LayoutInflater.from(context);
        this.mData=data;
        this.mLayoutId=layoutId;
        this.mContext=context;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(mMulitiTypeSupport != null){
            mLayoutId = viewType;
        }

        View inflate = mLayoutInflater.inflate(mLayoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(inflate);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {


        //点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mClickListener != null){
                    mClickListener.onItemClick(position);
                }
            }
        });

        //长按事件
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mLongClickListener != null){
                    return mLongClickListener.onItemLongClick(position);
                }
                return false;
            }
        });

        bindData(holder,mData.get(position),position);

    }

    @Override
    public int getItemViewType(int position) {
        if(mMulitiTypeSupport != null){
            return mMulitiTypeSupport.getLayoutId(mData.get(position));
        }

        return super.getItemViewType(position);
    }

    /**
     * 绑定数据
     * @param holder  viewholder
     * @param itemData      数据
     * @param position  位置
     */
    public abstract void bindData(ViewHolder holder, Data itemData, int position);

    @Override
    public int getItemCount() {
        return (mData==null)?0:mData.size();
    }


    //点击事件
    private OnItemClickListener mClickListener;
    public void setOnItemClickListener(OnItemClickListener listener){
        this.mClickListener=listener;
    }

    //长按事件
    private OnItemLongClickListener mLongClickListener;
    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        this.mLongClickListener = listener;
    }

}