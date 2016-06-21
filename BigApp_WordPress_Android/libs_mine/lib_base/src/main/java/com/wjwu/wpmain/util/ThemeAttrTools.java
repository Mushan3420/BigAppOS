package com.wjwu.wpmain.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by wjwu on 2015/9/10.
 */
public class ThemeAttrTools {

    public static int getValueOfColorAttr(Context context, int attrId) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrId, typedValue, true);
        return typedValue.resourceId;
    }

    public static String getValueOfColorStrAttr(Context context, int attrId) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrId, typedValue, true);
        return typedValue.string.toString();
    }
}
