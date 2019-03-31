package com.wzx.wzxfoundation.widget.headerfooterrecycleradapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wzx.wzxfoundation.R;

/**
 * 默认封装的HeaderFooterRecyclerViewAdapter，
 * 只有一个footer，并且footer有4个状态
 * footer带有数据加载状态
 */
public abstract class DefaultHFRecyclerAdapter extends HeaderFooterRecyclerViewAdapter<DefaultHFRecyclerAdapter.DefaultViewHolder> {
    public class DefaultViewHolder extends RecyclerView.ViewHolder {
        public DefaultViewHolder(View itemView) {
            super(itemView);
        }

        public void renderView(int position) {
        }
    }

    /**
     * 0 没有footer<br/>
     * 1 加载中...<br/>
     * 2 没有更多数据
     */
    private int mFooterStatus = 0;

    /**
     * 隐藏footer
     */
    public final void hideFooter() {
        try {
            if (mFooterStatus != 0) {
                mFooterStatus = 0;
                notifyFooterItemRemoved(0);
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    /**
     * footer loading 状态
     */
    public final void setFooterLoading() {
        try {
            if (mFooterStatus == 0) {
                mFooterStatus = 1;
                notifyFooterItemInserted(0);
            } else if (mFooterStatus != 1) {
                mFooterStatus = 1;
                notifyFooterItemChanged(0);
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    /**
     * footer 没有更多了
     */
    public final void setFooterNoMore() {
        try {
            if (mFooterStatus == 0) {
                mFooterStatus = 2;
                notifyFooterItemInserted(0);
            } else if (mFooterStatus != 2) {
                mFooterStatus = 2;
                notifyFooterItemChanged(0);
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected int getFooterItemCount() {
        return mFooterStatus == 0 ? 0 : 1;
    }

    @Override
    protected int getFooterItemViewType(int position) {
        return mFooterStatus;
    }

    @Override
    protected DefaultViewHolder onCreateFooterItemViewHolder(ViewGroup parent, int footerViewType) {
        View footerView = null;
        try {
            switch (footerViewType) {
                case 1://加载中...
                    footerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.wzx_defaultfooter_loading, parent, false);
                    break;
                case 2://没有更多数据了
                    footerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.wzx_defaultfooter_nomore, parent, false);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        DefaultViewHolder holder = createMyFooterViewHolder(footerView);
        return holder;
    }

    //***子类需要覆盖此方方提供返回的holder, 因为子类的holder都是不一样的, 继承VH的, 具体写法见下面注释
    protected DefaultViewHolder createMyFooterViewHolder(View footerView) {
        return new DefaultViewHolder(footerView);
    }

    @Override
    protected void onBindFooterItemViewHolder(DefaultViewHolder footerViewHolder, int position) {
        footerViewHolder.renderView(position);
    }

    //默认的头部实现 , 无头部, begin
    @Override
    protected int getHeaderItemCount() {
        return 0;
    }

    @Override
    protected DefaultViewHolder onCreateHeaderItemViewHolder(ViewGroup parent, int headerViewType) {
        return null;
    }

    @Override
    protected void onBindHeaderItemViewHolder(DefaultViewHolder headerViewHolder, int position) {
        if (headerViewHolder != null) {
            headerViewHolder.renderView(position);
        }
    }
    //默认的头部实现 , 无头部, end
}