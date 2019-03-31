package com.wzx.wzxfoundation.widget.headerfooterrecycleradapter;

import android.content.Context;
import android.support.v7.widget.LinearSmoothScroller;

/**
 * 辅助Recyceler滑动滚动指定条目到顶部的类
 * 默认的滚动到顶部只是条目可见就行了, 用这个可以把指定条目的顶和Recycleview的顶对齐
 *
 * //                    mManager.scrollToPositionWithOffset(1, 0);  //直接滚动
 *
 *                     //滑动滚动
 *                     final RecycleScrollTopScroller mScroller = new RecycleScrollTopScroller(mActivity);
 *                     mScroller.setTargetPosition(1);
 *                     mManager.startSmoothScroll(mScroller);
 */
public class RecycleScrollTopScroller extends LinearSmoothScroller {
    public RecycleScrollTopScroller(Context context) {
        super(context);
    }

    /**
     *     /**
     *      * 指定滚动停留位置
     *      * @return {@link #LinearSmoothScroller#SNAP_TO_START}，{@link #LinearSmoothScroller#SNAP_TO_END},{@link #LinearSmoothScroller#SNAP_TO_ANY}
     *      * 1.将子视图的左侧或顶部与父视图的左侧或顶部对齐；
     *      * 2.将子视图的右侧或底部与父视图的右侧或底部对齐；
     *      * 3.具体取决于其当前与其父代相关的位置,也是默认设置。
     *      * /
     * @return
     */
    @Override
    protected int getHorizontalSnapPreference() {
        return SNAP_TO_START;//具体见源码注释
    }
    @Override
    protected int getVerticalSnapPreference() {
        return SNAP_TO_START;//具体见源码注释
    }
}
