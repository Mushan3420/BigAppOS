package event;

import android.util.Log;
import android.widget.Toast;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.util.AsyncExecutor;
import de.greenrobot.event.util.ThrowableFailureEvent;

/**
 * Created by wjwu on 2015/8/29.
 */
public class Test2Event {

    public final String message;

    public Test2Event(String message) {
        this.message = message;
    }


    // This method will be called when a MessageEvent is posted 不同线程发出来的，此处也是得到
    public void onEvent(TestEvent event) {
        Log.e("", "1111111111111");
        //这个拦截后，后面的就收不到，只能在onEvent中生效，并且需要是同一线程
        EventBus.getDefault().cancelEventDelivery(event);
    }

    public void onEventMainThread(TestEvent event) {
        Log.e("", "222222222222");
        // 新开子线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new AsyncEvent("dd"));
            }
        }).start();
        // Change some UI
    }

    public void onEventMainThread(ThrowableFailureEvent event) {
        Log.e("", "222222222222");
        // Show error in UI
    }

    // Called in a separate thread
    public void onEventAsync(TestEvent event) {
        //不能执行UI操作
//        Toast.makeText(this, event.message + " onEventAsync", Toast.LENGTH_SHORT).show();
        Log.e("", "333333333333");
    }

    // Called in the background thread
    public void onEventBackgroundThread(TestEvent event) {
        Log.e("", "444444444444");
        //不能执行UI操作，不然会抛异常
        Log.e("", "44444444444 这里没有执行");
    }

    // This method will be called when a MessageEvent is posted
    // Called in the same thread (default)
    public void onEvent(Test2Event event) {
        AsyncExecutor.create().execute(
                new AsyncExecutor.RunnableEx() {
                    @Override
                    public void run() throws Exception {
//                        EventBus.getDefault().postSticky(new TestEvent("jejej"));
                        EventBus.getDefault().post(new TestEvent("jejej"));
                    }
                });
    }
}
