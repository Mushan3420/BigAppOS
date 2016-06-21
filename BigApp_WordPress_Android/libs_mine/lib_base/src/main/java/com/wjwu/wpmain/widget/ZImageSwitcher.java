package com.wjwu.wpmain.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wjwu.wpmain.lib_base.R;
import com.wjwu.wpmain.util.ImageLoaderOptions;

import java.util.Timer;
import java.util.TimerTask;

public class ZImageSwitcher extends ImageSwitcher {
    private Timer mTimer;
    private GestureDetector detector;
    private Context context;
    private int index;// 图片位置索引
    private int[] imageIds;
    private String[] uris;
    public boolean isFling = false;// 是否正在滑动
    private int direction = 1;// 方向：1为向左滑动；-1为向右滑动
    private AdapterView.OnItemClickListener mListener;
    private ScrolledBottomListener mSListener;

    private FlowIndicator mIndicator;
    private ImageLoader imageLoader;
    private DisplayImageOptions displayImageOptions;
    private boolean TimerStart = false;

    public ZImageSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.imageLoader = ImageLoader.getInstance();
        this.displayImageOptions = ImageLoaderOptions.getOptionsCachedBoth();
        this.detector = new GestureDetector(context, new MyOnGestureListener());
        this.setLongClickable(true);
    }

    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1 && !isFling) {
                setImage(direction);
            }
        }
    };

    class MyOnGestureListener extends SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            if (getViewCount() <= 1)
                return false;
            isFling = true;
            float instance = e1.getX() - e2.getX();
            if (instance > 10) {
                direction = 1;
                index++;
                if (index > getViewCount() - 1) {
                    if (mSListener != null) {
                        mSListener.onScrolled(direction);
                        index = getViewCount() - 1;
                        isFling = false;
                        return true;
                    } else {
                        index = 0;
                    }
                }
                setImage(direction);
                resetSwitching();
            } else if (instance < -10) {
                direction = -1;
                index--;
                if (index < 0) {
                    if (mSListener != null) {
                        mSListener.onScrolled(direction);
                        index = 0;
                        isFling = false;
                        return true;
                    } else {
                        index = getViewCount() - 1;
                    }
                }
                setImage(direction);
                resetSwitching();
            }
            isFling = false;
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (mListener != null) {
                mListener.onItemClick(null, getChildAt(index), index, index);
            }
            return true;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return detector.onTouchEvent(event);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public void setFlowIndicator(FlowIndicator flowIndicator) {
        mIndicator = flowIndicator;
        flowIndicator.setImageSwitcher(this);
    }

    public ScrolledBottomListener getHorizontalScrollListener() {
        return mSListener;
    }

    public void setScrolledBottomListener(ScrolledBottomListener mListener) {
        this.mSListener = mListener;
    }

    /**
     * 初始化
     *
     * @param imageIds
     */
    public void setImageViews(int[] imageIds) {
        if (imageIds == null || imageIds.length <= 0) {
            return;
        }
        this.imageIds = imageIds;
        this.uris = null;
        this.reset();
        this.setFactory(new MyFactory());
        this.setImageResource(imageIds[index]);
        invalidate();
        requestLayout();
    }

    /**
     * 初始化
     *
     * @param uris
     */
    public void setUri(String[] uris) {
        if (uris == null || uris.length <= 0) {
            return;
        }
        this.imageIds = null;
        this.uris = uris;
        this.removeAllViews();
        this.reset();
        this.setFactory(new MyFactory());
        if (index < uris.length) {
            this.setImageBitmap(uris[index]);
        }
        invalidate();
        requestLayout();
    }

    public void setImageBitmap(String url) {
        ImageView image = (ImageView) this.getNextView();
        imageLoader.displayImage(url, image, displayImageOptions);
        showNext();
    }

    class MyFactory implements ViewFactory {

        @Override
        public View makeView() {
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageView.setAdjustViewBounds(true);
//            imageView.setMaxWidth();
            imageView.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            return imageView;
        }
    }

    /**
     * 启动线程切换图片
     */
    public void startSwitching() {
        if (imageIds != null && imageIds.length <= 1) {
            return;
        } else if (uris != null && getUriLength() <= 1) {
            return;
        }
        if (!TimerStart) {
            TimerStart = true;
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (direction == 1) {
                        index++;
                    } else {
                        index--;
                    }
                    if (index > getViewCount() - 1) {
                        index = 0;
                    }
                    if (index < 0) {
                        index = getViewCount() - 1;
                    }
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }
            }, 5 * 1000, 5 * 1000);
        }
    }

    public void resetSwitching() {
        if (mListener != null) {
            stopSwitching();
            startSwitching();
        }
    }

    public void stopSwitching() {
        if (mTimer != null) {
            TimerStart = false;
            mTimer.cancel();
            mTimer.purge();
            handler.removeMessages(1);
        }
    }

    public int getViewCount() {
        if (imageIds != null) {
            return imageIds.length;
        } else if (uris != null) {
            return uris.length;
        }
        return 0;
    }

    public int getUriLength() {
        int length = 0;
        if (uris != null) {
            for (int i = 0; i < uris.length; i++) {
                if (!TextUtils.isEmpty(uris[i])) {
                    length++;
                }
            }
        }
        return length;
    }

    /**
     * 根据方向设置图片和动画效果
     *
     * @param direction
     */
    private void setImage(int direction) {
        if (mIndicator != null) {
            mIndicator.onSwitched(this, index);
        }

        if (direction == 1) {
            setInAnimation(context, R.anim.push_left_in);
            setOutAnimation(context, R.anim.push_left_out);
            if (imageIds != null) {
                setImageResource(imageIds[index]);
            } else if (uris != null) {
                setImageBitmap(uris[index]);
            }
        } else {
            setInAnimation(context, R.anim.push_right_in);
            setOutAnimation(context, R.anim.push_right_out);
            if (imageIds != null) {
                setImageResource(imageIds[index]);
            } else if (uris != null) {
                setImageBitmap(uris[index]);
            }
        }
    }

    /**
     * Receives call backs when a new {@link View} has been scrolled to.
     */
    public interface ViewSwitchListener {
        void onSwitched(View view, int position);
    }

    public interface ScrolledBottomListener {
        void onScrolled(int type);
    }

}
