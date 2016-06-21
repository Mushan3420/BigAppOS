package com.wjwu.wpmain.uzwp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.animation.Interpolator;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.wjwu.wpmain.cache.FileCache;
import com.wjwu.wpmain.cache.SpTool;
import com.wjwu.wpmain.lib_base.BaseApplication;
import com.wjwu.wpmain.util.CommonUtils;
import com.wjwu.wpmain.util.ScreenTools;
import com.wjwu.wpmain.util.ZSDKUtils;

import de.greenrobot.event.EventBus;
import event.MoonEvent;
import model.VersionUpdate;

public class ActivityMainSliding extends SlidingFragmentActivity {
    private FragmentMainHome mFragmentMainHome;
    private FragmentSlidingMenu mFragmentSlidingMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        if (new SpTool(this, SpTool.SP_SETTING).getBoolean("moon", false)) {
            BaseApplication.sDefaultImageDrawable = R.drawable.z_iv_default_y;
            setTheme(R.style.ActivityThemeMoon);
        } else {
            BaseApplication.sDefaultImageDrawable = R.drawable.z_iv_default;
            setTheme(R.style.ActivityTheme);
        }
        // set the Above View
        if (savedInstanceState != null) {
            mFragmentMainHome = (FragmentMainHome) getSupportFragmentManager().getFragment(savedInstanceState, "mFragmentMainHome");
        }
        if (mFragmentMainHome == null) {
            mFragmentMainHome = new FragmentMainHome();
            mFragmentMainHome.setArguments(getIntent().getExtras());
        }
        setContentView(R.layout.activity_main_sliding);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, mFragmentMainHome)
                .commit();

        // set the Behind View
        setBehindContentView(R.layout.v_layout_sliding_menu);
        if (savedInstanceState != null) {
            mFragmentSlidingMenu = (FragmentSlidingMenu) this.getSupportFragmentManager().findFragmentById(R.id.menu_frame);
        }
        if (mFragmentSlidingMenu == null) {
            mFragmentSlidingMenu = new FragmentSlidingMenu();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.menu_frame, mFragmentSlidingMenu)
                    .commit();
        }

        // customize the SlidingMenu
        SlidingMenu sm = getSlidingMenu();
        sm.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
        sm.setShadowDrawable(R.drawable.v_sliding_menu_shadow);
        sm.setBehindOffset(ScreenTools.getScreenParams(this).width * 3 / 10);
        sm.setFadeDegree(0.35f);
        //设置触摸方式，必须为 TOUCHMODE_FULLSCREEN(全屏可触摸)，TOUCHMODE_MARGIN(边缘可触摸)，默认 48dp, TOUCHMODE_NONE(不可触摸)三者之一
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        //动画效果
        sm.setBehindScrollScale(0.0f);
        sm.setBackgroundColor(Color.parseColor("#252525"));
        sm.setBehindCanvasTransformer(getTransformer(1));

//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "mFragmentMainHome", mFragmentMainHome);
    }

    /***
     * sliding menu show anim
     *
     * @param type 1->zoom, 2->scale, default->slide up
     * @return CanvasTransformer
     */
    private SlidingMenu.CanvasTransformer getTransformer(int type) {
        switch (type) {
            case 1:
                return new SlidingMenu.CanvasTransformer() {
                    @Override
                    public void transformCanvas(Canvas canvas, float percentOpen) {
                        float scale = (float) (percentOpen * 0.25 + 0.75);
                        canvas.scale(scale, scale, canvas.getWidth() / 2, canvas.getHeight() / 2);
                    }
                };
            case 2:
                return new SlidingMenu.CanvasTransformer() {
                    @Override
                    public void transformCanvas(Canvas canvas, float percentOpen) {
                        canvas.translate(0, canvas.getHeight() * (1 - interp.getInterpolation(percentOpen)));
                    }
                };
            default:
                return new SlidingMenu.CanvasTransformer() {
                    @Override
                    public void transformCanvas(Canvas canvas, float percentOpen) {
                        canvas.scale(percentOpen, 1, 0, 0);
                    }
                };
        }
    }

    private static Interpolator interp = new Interpolator() {
        @Override
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t + 1.0f;
        }
    };

    // Called in Android UI's main thread
    @SuppressLint("NewApi")
    public void onEventMainThread(MoonEvent event) {
        if (ZSDKUtils.hasHoneycomb()) {
            recreate();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private long mExitAppTime = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mExitAppTime < 2000) {
            finish();
        } else {
            mExitAppTime = System.currentTimeMillis();
            Toast.makeText(this, getString(com.wjwu.wpmain.lib_base.R.string.z_toast_exit_app),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
