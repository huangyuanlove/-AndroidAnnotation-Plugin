package com.huangyuanlove.plugin;

import java.util.HashMap;

public class Constant {

    public static final HashMap<String, String> paths = new HashMap<>();

    static {
        // special classes; default package is android.widget.*
        paths.put("WebView", "android.webkit.WebView");
        paths.put("View", "android.view.View");
        paths.put("ViewStub", "android.view.ViewStub");
        paths.put("SurfaceView", "android.view.SurfaceView");
        paths.put("TextureView", "android.view.TextureView");
    }
}
