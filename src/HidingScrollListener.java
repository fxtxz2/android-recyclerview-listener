package com.crazyspread.homepage.listener;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * RecyclerView 上浮隐藏
 * Created by zyl on 16/9/1.
 */
public abstract class HidingScrollListener extends RecyclerView.OnScrollListener {

    private static int HIDE_THRESHOLD = 0;
    private int scrolledDistance = 0;
    private boolean controlsVisible = true;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        BigDecimal maxHeight = new BigDecimal(recyclerView.getHeight());
        BigDecimal count = new BigDecimal(recyclerView.getChildCount());
        if (HIDE_THRESHOLD == 0){
            HIDE_THRESHOLD = maxHeight.divide(count, 0, RoundingMode.CEILING).intValue();
        }
        //show views if first item is first visible position and views are hidden
        if (firstVisibleItem == 0) {
            if(!controlsVisible) {
                onShow();
                controlsVisible = true;
            }
        } else {
            if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                onHide();
                controlsVisible = false;
                scrolledDistance = 0;
            } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                onShow();
                controlsVisible = true;
                scrolledDistance = 0;
            }
        }
        if((controlsVisible && dy>0) || (!controlsVisible && dy<0)) {
            scrolledDistance += dy;
        }
    }
    public abstract void onHide();
    public abstract void onShow();

}
