package event;

import android.webkit.WebSettings;

/**
 * Created by wjwu on 2015/9/4.
 */
public class WebTxtChangedEvent {
    public WebSettings.TextSize textSize;

    public WebTxtChangedEvent(WebSettings.TextSize textSize) {
        this.textSize = textSize;
    }
}
