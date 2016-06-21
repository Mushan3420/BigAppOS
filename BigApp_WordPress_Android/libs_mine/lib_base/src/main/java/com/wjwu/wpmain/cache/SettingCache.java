package com.wjwu.wpmain.cache;

import android.content.Context;
import android.webkit.WebSettings;

/**
 * Created by wjwu on 2015/9/3.
 */
public class SettingCache {

    public static void putFontSize(Context context, WebSettings.TextSize size) {
        int sizeInt = 2;
        if (size == WebSettings.TextSize.LARGER) {
            sizeInt = 3;
        } else if (size == WebSettings.TextSize.LARGEST) {
            sizeInt = 4;
        } else if (size == WebSettings.TextSize.NORMAL) {
            sizeInt = 2;
        } else if (size == WebSettings.TextSize.SMALLER) {
            sizeInt = 1;
        }
        new SpTool(context, SpTool.SP_SETTING).putInt("font_size", sizeInt);
    }

    public static WebSettings.TextSize getFontSize(Context context) {
        int size = new SpTool(context, SpTool.SP_SETTING).getInt("font_size", 2);
        if (size == 3) {
            return WebSettings.TextSize.LARGER;
        } else if (size == 4) {
            return WebSettings.TextSize.LARGEST;
        } else if (size == 2) {
            return WebSettings.TextSize.NORMAL;
        } else if (size == 1) {
            return WebSettings.TextSize.SMALLER;
        }
        return WebSettings.TextSize.NORMAL;
    }

    public static void putImgMode(Context context, int img_mode) {
        new SpTool(context, SpTool.SP_SETTING).putInt("img_mode", img_mode);
    }

    /***
     * @param context
     * @return 1无图, 2低质量, 3高质量（原图）, 默认为高质量
     */
    public static int getImgMode(Context context) {
        return new SpTool(context, SpTool.SP_SETTING).getInt("img_mode", 3);
    }
}
