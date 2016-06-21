package com.wjwu.wpmain.drag_tab;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wjwu.wpmain.cache.FileCache;
import com.wjwu.wpmain.cache.SpTool;
import com.wjwu.wpmain.uzwp.R;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import event.UserCatalogChangedEvent;
import model.NavCatalog;

/**
 * 频道管理
 */
public class CatalogActivity extends ActivityBaseGesture implements OnItemClickListener {
    /**
     * 用户栏目的GRIDVIEW
     */
    private DragGrid userGridView;
    /**
     * 其它栏目的GRIDVIEW
     */
    private OtherGridView otherGridView;
    /**
     * 用户栏目对应的适配器，可以拖动
     */
    DragAdapter userAdapter;
    /**
     * 其它栏目对应的适配器
     */
    OtherAdapter otherAdapter;
    /**
     * 其它栏目列表
     */
    ArrayList<NavCatalog> otherChannelList = new ArrayList<NavCatalog>();
    /**
     * 用户栏目列表
     */
    ArrayList<NavCatalog> userChannelList = new ArrayList<NavCatalog>();
    /**
     * 是否在移动，由于这边是动画结束后才进行的数据更替，设置这个限制为了避免操作太频繁造成的数据错乱。
     */
    boolean isMove = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (new SpTool(this, SpTool.SP_SETTING).getBoolean("moon", false)) {
            setTheme(R.style.ActivityThemeAnimUpDownMoon);
        } else {
            setTheme(R.style.ActivityThemeAnimUpDown);
        }
        setContentView(R.layout.drag_activity);
        initView();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        userChannelList = FileCache.getCatalogUser();
        otherChannelList = FileCache.getCatalogOthers();
        if (otherChannelList == null) {
            otherChannelList = new ArrayList<>();
        }
        userAdapter = new DragAdapter(this, userChannelList);
        userGridView.setAdapter(userAdapter);
        otherAdapter = new OtherAdapter(this, otherChannelList);
        otherGridView.setAdapter(otherAdapter);
    }

    /**
     * 初始化布局
     */
    private void initView() {
        userGridView = (DragGrid) findViewById(R.id.gridview);
        otherGridView = (OtherGridView) findViewById(R.id.gridview_2);
        //设置GRIDVIEW的ITEM的点击监听
        otherGridView.setOnItemClickListener(this);
        userGridView.setOnItemClickListener(this);
        findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     * GRIDVIEW对应的ITEM点击监听接口
     */
    @Override
    public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
        //如果点击的时候，之前动画还没结束，那么就让点击事件无效
        if (isMove) {
            return;
        }
        if (parent.getId() == R.id.gridview) {
            //position为 0，1 的不可以进行任何操作
            if (position != 0) {
                final ImageView moveImageView = getView(view);
                if (moveImageView != null) {
                    TextView newTextView = (TextView) view.findViewById(R.id.tv_item);
                    final int[] startLocation = new int[2];
                    newTextView.getLocationInWindow(startLocation);
                    final NavCatalog channel = ((DragAdapter) parent.getAdapter()).getItem(position);//获取点击的频道内容
                    otherAdapter.setVisible(false);
                    //添加到最后一个
                    otherAdapter.addItem(channel);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            try {
                                int[] endLocation = new int[2];
                                //获取终点的坐标
                                otherGridView.getChildAt(otherGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
                                MoveAnim(moveImageView, startLocation, endLocation, channel, userGridView);
                                userAdapter.setRemove(position);
                            } catch (Exception localException) {
                            }
                        }
                    }, 50L);
                }
            }
        } else if (parent.getId() == R.id.gridview_2) {
            final ImageView moveImageView = getView(view);
            if (moveImageView != null) {
                TextView newTextView = (TextView) view.findViewById(R.id.tv_item);
                final int[] startLocation = new int[2];
                newTextView.getLocationInWindow(startLocation);
                final NavCatalog channel = ((OtherAdapter) parent.getAdapter()).getItem(position);
                userAdapter.setVisible(false);
                //添加到最后一个
                userAdapter.addItem(channel);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        try {
                            int[] endLocation = new int[2];
                            //获取终点的坐标
                            userGridView.getChildAt(userGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
                            MoveAnim(moveImageView, startLocation, endLocation, channel, otherGridView);
                            otherAdapter.setRemove(position);
                        } catch (Exception localException) {
                        }
                    }
                }, 50L);
            }
        }

    }

    /**
     * 点击ITEM移动动画
     *
     * @param moveView
     * @param startLocation
     * @param endLocation
     * @param moveChannel
     * @param clickGridView
     */
    private void MoveAnim(View moveView, int[] startLocation, int[] endLocation, final NavCatalog moveChannel,
                          final GridView clickGridView) {
        int[] initLocation = new int[2];
        //获取传递过来的VIEW的坐标
        moveView.getLocationInWindow(initLocation);
//得到要移动的VIEW,并放入对应的容器中
        final ViewGroup moveViewGroup = getMoveViewGroup();
        final View mMoveView = getMoveView(moveViewGroup, moveView, initLocation);
        //创建移动动画
        TranslateAnimation moveAnimation = new TranslateAnimation(
                startLocation[0], endLocation[0], startLocation[1],
                endLocation[1]);
        moveAnimation.setDuration(300L);//动画时间
        //动画配置
        AnimationSet moveAnimationSet = new AnimationSet(true);
        moveAnimationSet.setFillAfter(false);//动画效果执行完毕后，View对象不保留在终止的位置
        moveAnimationSet.addAnimation(moveAnimation);
        mMoveView.startAnimation(moveAnimationSet);
        moveAnimationSet.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                isMove = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                moveViewGroup.removeView(mMoveView);
                // instanceof 方法判断2边实例是不是一样，判断点击的是DragGrid还是OtherGridView
                if (clickGridView instanceof DragGrid) {
                    otherAdapter.setVisible(true);
                    otherAdapter.notifyDataSetChanged();
                    userAdapter.remove();
                } else {
                    userAdapter.setVisible(true);
                    userAdapter.notifyDataSetChanged();
                    otherAdapter.remove();
                }
                isMove = false;
            }
        });
    }

    /**
     * 获取移动的VIEW，放入对应ViewGroup布局容器
     *
     * @param viewGroup
     * @param view
     * @param initLocation
     * @return
     */
    private View getMoveView(ViewGroup viewGroup, View view, int[] initLocation) {
        int x = initLocation[0];
        int y = initLocation[1];
        viewGroup.addView(view);
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mLayoutParams.leftMargin = x;
        mLayoutParams.topMargin = y;
        view.setLayoutParams(mLayoutParams);
        return view;
    }

    /**
     * 创建移动的ITEM对应的ViewGroup布局容器
     */
    private ViewGroup getMoveViewGroup() {
        ViewGroup moveViewGroup = (ViewGroup) getWindow().getDecorView();
        LinearLayout moveLinearLayout = new LinearLayout(this);
        moveLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        moveViewGroup.addView(moveLinearLayout);
        return moveLinearLayout;
    }

    /**
     * 获取点击的Item的对应View，
     *
     * @param view
     * @return
     */
    private ImageView getView(View view) {
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        ImageView iv = new ImageView(this);
        iv.setImageBitmap(cache);
        return iv;
    }

    /**
     * 退出时候保存选择后数据库的设置
     */
    private void saveChannel() {
        FileCache.saveCatalogUser(userChannelList);
        FileCache.saveCatalogOthers(otherChannelList);
        EventBus.getDefault().postSticky(new UserCatalogChangedEvent(userChannelList));
    }

    @Override
    public void onBackPressed() {
        if (userAdapter.isListChanged()) {
            saveChannel();
            finish();
        } else {
            super.onBackPressed();
        }
    }

    public static void gotoCatalogActivity(Context context) {
        Intent intent = new Intent(context, CatalogActivity.class);
        context.startActivity(intent);
    }
}
