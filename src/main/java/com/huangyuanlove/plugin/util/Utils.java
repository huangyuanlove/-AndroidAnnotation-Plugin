package com.huangyuanlove.plugin.util;

import com.huangyuanlove.plugin.ui.ElementBean;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Locale;

public class Utils {
    public static int getInjectCount(ArrayList<ElementBean> elements) {
        int cnt = 0;
        for (ElementBean element : elements) {
            if (element.used) {
                cnt++;
            }
        }
        return cnt;
    }
    public static int getClickCount(ArrayList<ElementBean> elements) {
        int cnt = 0;
        for (ElementBean element : elements) {
            if (element.isClick) {
                cnt++;
            }
        }
        return cnt;
    }
    public static String capitalize(@Nullable String src) {
        if (src == null) {
            return null;
        }
        return src.substring(0, 1).toUpperCase(Locale.US) + src.substring(1);
    }

}
