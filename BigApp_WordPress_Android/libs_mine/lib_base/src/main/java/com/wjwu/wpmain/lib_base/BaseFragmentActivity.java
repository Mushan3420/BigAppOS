package com.wjwu.wpmain.lib_base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

import com.wjwu.wpmain.anim.Rotate3dAnimation;
import com.wjwu.wpmain.cache.SpTool;
import com.wjwu.wpmain.lib_base.R;
import com.wjwu.wpmain.util.ZSDKUtils;

import de.greenrobot.event.EventBus;
import event.MoonEvent;

/***
 * Activity父类，主要用于统一控制进入和退出动画
 *
 * @author AG
 */
public class BaseFragmentActivity extends FragmentActivity {
    public static final String ARG_FRAGMENT_TAG = "fragmentTag";

    private View mContainer_activity;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    /***
     * 显示fragment + 动画效果
     *
     * @param layoutId container_id
     * @param fragment fragment
     * @param extras   bundle
     */
    protected void showFragmentLeftRight(int layoutId, Fragment fragment, Bundle extras, boolean back,
                                         String fragmentTag) {
        if (fragment != null) {
            if (extras != null) {
                fragment.setArguments(extras);
            }
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();

            if (back) {
                transaction.setCustomAnimations(R.anim.v_left_in,
                        R.anim.v_right_out, R.anim.v_right_in, R.anim.v_left_out);
            } else {
                // 定义进入和退出的动画
                transaction.setCustomAnimations(R.anim.v_right_in,
                        R.anim.v_left_out, R.anim.v_left_in, R.anim.v_right_out);
            }
            try {
                transaction.replace(layoutId, fragment, fragmentTag).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * 显示跳转界面, add
     *
     * @param fragment fragment
     * @param extras   bundle
     * @param back     是否返回true or false
     */
    protected void showFragmentAdd(int layoutId,
                                   Fragment fragment, Bundle extras, boolean back,
                                   String fragmentTag) {
        if (fragment != null) {
            if (extras != null) {
                fragment.setArguments(extras);
            }
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            if (back) {
                transaction.setCustomAnimations(R.anim.v_roll_up,
                        R.anim.v_roll_down);
            } else {
                // 定义进入和退出的动画
                transaction.setCustomAnimations(R.anim.v_roll_up,
                        R.anim.v_roll_down, R.anim.v_roll_up,
                        R.anim.v_roll_down);
            }
            transaction.addToBackStack(fragmentTag);
            transaction.add(layoutId, fragment, fragmentTag).commit();
        }
    }

    /***
     * 显示fragment 不加动画效果
     *
     * @param layoutId container_id
     * @param fragment fragment
     */
    protected void showFragment(int layoutId, Fragment fragment, Bundle extras,
                                String fragmentTag) {
        if (fragment != null) {
            if (extras != null) {
                fragment.setArguments(extras);
            }
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            try {
                transaction.replace(layoutId, fragment, fragmentTag).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setmContainer_activity(View container_activity) {
        mContainer_activity = container_activity;
    }

    public void showFragmentRotation(final Fragment fragment, boolean back) {
        boolean zheng = true;
        int start = 0;
        int end = 90;
        if (back) {
            zheng = false;
            end = -90;
        }
        if (mContainer_activity == null) {
            return;
        }
        // Find the center of the container
        final float centerX = mContainer_activity.getWidth() / 2.0f;
        final float centerY = mContainer_activity.getHeight() / 2.0f;
        // Create a new 3D rotation with the supplied parameter
        // The animation listener is used to trigger the next animation
        final Rotate3dAnimation rotation = new Rotate3dAnimation(
                start, end, centerX, centerY, 310.0f, true);
        rotation.setDuration(300);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new DisplayNextView(zheng, fragment));// 添加监听执行现实内容的切换
        mContainer_activity.startAnimation(rotation);// 执行上半场翻转动画
    }

    private final class DisplayNextView implements Animation.AnimationListener {
        private final boolean mPosition;
        private final Fragment mfragment;

        private DisplayNextView(boolean zheng, Fragment fragment) {
            mPosition = zheng;
            mfragment = fragment;
        }

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            mContainer_activity.post(new SwapViews(mPosition, mfragment));// 添加新的View
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }

    private final class SwapViews implements Runnable {
        private final boolean mPosition;
        private final Fragment mfragment;

        public SwapViews(boolean position, Fragment fragment) {
            mPosition = position;
            mfragment = fragment;
        }

        public void run() {
            final float centerX = mContainer_activity.getWidth() / 2.0f;
            final float centerY = mContainer_activity.getHeight() / 2.0f;
            Rotate3dAnimation rotation;
            FragmentTransaction tration = getSupportFragmentManager()
                    .beginTransaction();
            tration.replace(R.id.container_activity, mfragment);
            if (mPosition) {
                rotation = new Rotate3dAnimation(-90, 0, centerX, centerY,
                        310.0f, false);
            } else {
                rotation = new Rotate3dAnimation(90, 0, centerX, centerY,
                        310.0f, false);
            }
            tration.commit();
            rotation.setDuration(300);
            rotation.setFillAfter(true);
            rotation.setInterpolator(new DecelerateInterpolator());
            mContainer_activity.startAnimation(rotation);
        }
    }

    /***
     * fragment跳转接口
     *
     * @author nxp71465
     */
    public interface FragmentCallBack {

        /***
         * 跳转指定的fragment
         *
         * @param extras 传递数据
         * @param back   是否返回
         */
        void fragmentChanged(String fragmentTag, Bundle extras, boolean back);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
    }

    // Called in Android UI's main thread
    @SuppressLint("NewApi")
    public void onEventMainThread(MoonEvent event) {
        if (ZSDKUtils.hasHoneycomb()) {
            recreate();
        }
    }
}
