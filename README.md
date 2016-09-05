# android-recyclerview-listener
实现了列表的上滑手势上浮隐藏，类似Toolbar效果，主要参考了http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0317/2612.html
# 最终效果
![上浮隐藏](./20160905.gif)

# 使用
## 设置HidingScrollListener监听
```Java
// 上下滚动隐藏和显示
mRecyclerView.addOnScrollListener(new HidingScrollListener() {
    @Override
    public void onHide() {
        hideViews();// 你自己的隐藏动画
    }

    @Override
    public void onShow() {
        showViews();// 你自己的显示动画
    }
});
```
## 我是怎么做的
```java
/**
  *下拉刷新恢复
  */
private void showViews() {
  // 计算下移最大高度
  int height = my_profit.getHeight() + tabs.getHeight();
  // 恢复原来位置
  my_profit.animate().translationY(0).setInterpolator(new AccelerateDecelerateInterpolator()).start();
  // tab栏恢复原来位置
  tabs.animate().translationY(0).setInterpolator(new AccelerateDecelerateInterpolator()).start();
  // 列表刷新view下移最大高度
  swiperefresh.animate().translationY(+height).setInterpolator(new AccelerateDecelerateInterpolator()).start();
  // 无数据view下移最大高度
  view_no_data.animate().translationY(+height).setInterpolator(new AccelerateDecelerateInterpolator()).start();

  // 记住该布局整体高度
  if (maxHeight == 0 ){
      maxHeight = mainView.getHeight();
  }
  // 动态变化列表刷新view高度
  ValueAnimator valueAnimator1 = ValueAnimator.ofInt(swiperefresh.getHeight(), maxHeight - height);
  valueAnimator1.setInterpolator(new AccelerateDecelerateInterpolator());
  valueAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator valueAnimator) {
          Integer value = (Integer) valueAnimator.getAnimatedValue();
          swiperefresh.getLayoutParams().height = value.intValue();
          swiperefresh.requestLayout();
      }
  });
  // 动态变化列表view高度
  ValueAnimator valueAnimator2 = ValueAnimator.ofInt(view_no_data.getHeight(), maxHeight - height);
  valueAnimator2.setInterpolator(new AccelerateDecelerateInterpolator());
  valueAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator valueAnimator) {
          Integer value = (Integer) valueAnimator.getAnimatedValue();
          view_no_data.getLayoutParams().height = value.intValue();
          view_no_data.requestLayout();
      }
  });

  AnimatorSet animatorSet = new AnimatorSet();
  animatorSet.playTogether(valueAnimator1, valueAnimator2);
  animatorSet.start();
}

/**
  *上浮隐藏
  */
private void hideViews() {
  int height = my_profit.getHeight();
  my_profit.animate().translationY(-height).setInterpolator(new AccelerateDecelerateInterpolator()).start();
  tabs.animate().translationY(-height).setInterpolator(new AccelerateDecelerateInterpolator()).start();
  swiperefresh.animate().translationY(+tabs.getHeight()).setInterpolator(new AccelerateDecelerateInterpolator()).start();
  view_no_data.animate().translationY(+tabs.getHeight()).setInterpolator(new AccelerateDecelerateInterpolator()).start();

  if (maxHeight == 0){
      maxHeight = mainView.getHeight();
  }
  ValueAnimator valueAnimator1 = ValueAnimator.ofInt(swiperefresh.getHeight(), maxHeight - tabs.getHeight());
  valueAnimator1.setInterpolator(new AccelerateDecelerateInterpolator());
  valueAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator valueAnimator) {
          Integer value = (Integer) valueAnimator.getAnimatedValue();
          swiperefresh.getLayoutParams().height = value.intValue();
          swiperefresh.requestLayout();
      }
  });

  ValueAnimator valueAnimator2 = ValueAnimator.ofInt(view_no_data.getHeight(), maxHeight - tabs.getHeight());
  valueAnimator2.setInterpolator(new AccelerateDecelerateInterpolator());
  valueAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator valueAnimator) {
          Integer value = (Integer) valueAnimator.getAnimatedValue();
          view_no_data.getLayoutParams().height = value.intValue();
          view_no_data.requestLayout();
      }
  });

  AnimatorSet animatorSet = new AnimatorSet();
  animatorSet.playTogether(valueAnimator1, valueAnimator2);
  animatorSet.start();
}
```
# 我的布局
根布局是一个FrameLayout，然后利用上下移动的动画来实现上浮隐藏。
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/white"
    android:id="@+id/fragmentRootView">

    <android.support.v4.widget.SwipeRefreshLayout
        android:id="@+id/swiperefresh"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        <android.support.v7.widget.RecyclerView
            android:id="@+id/my_recycler_view"
            android:scrollbars="vertical"
            android:dividerHeight="5dp"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>
    </android.support.v4.widget.SwipeRefreshLayout>

    <include
        android:id="@+id/view_no_data"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        layout="@layout/view_no_data"
        android:visibility="gone" />

    <!-- 顶部标签 -->
    <LinearLayout
        android:id="@+id/toolbarContainer"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical">
        <!--我的收益部分-->
        <include
            android:id="@+id/my_profit"
            layout="@layout/home_profit_disc"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="0dp" />
        <RelativeLayout
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="0"
            android:background="@android:color/white" >
            <android.support.v4.view.ViewPager
                android:id="@+id/gallery"
                android:layout_centerInParent="true"
                android:layout_width="match_parent"
                android:layout_height="match_parent" />

            <com.viewpagerindicator.CirclePageIndicator
                xmlns:app="http://schemas.android.com/apk/res-auto"
                android:id="@+id/gallery_indicator"
                android:layout_alignParentBottom="true"
                android:layout_width="match_parent"
                android:layout_height="10dp"
                android:layout_marginBottom="3dp"
                app:fillColor="@color/list_value"
                app:pageColor="@color/task_label_color_default"
                app:strokeWidth="0dp"/>

        </RelativeLayout>
        <include android:id="@+id/tabs"
            layout="@layout/my_task_hall_header" />
    </LinearLayout>
</FrameLayout>
```
