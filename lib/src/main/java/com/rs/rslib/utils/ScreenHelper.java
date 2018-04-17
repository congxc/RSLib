package com.rs.rslib.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.lang.reflect.Field;

/**
 * 获取屏幕宽高
 */
public class ScreenHelper {

    /**
     * The type Screen.
     */
    public static class Screen {
        /**
         * The Width pixels.
         */
        public int widthPixels;
        /**
         * The Height pixels.
         */
        public int heightPixels;
        /**
         * The Density dpi.
         */
        public int densityDpi;
        /**
         * The Density.
         */
        public float density;
    }

    /**
     * Gets screen pixels.
     *
     * @param context the context
     * @return the screen pixels
     */
    public static Screen getScreenPixels(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        Screen screen = new Screen();
        screen.widthPixels = dm.widthPixels;// e.g. 1080
        screen.heightPixels = dm.heightPixels;// e.g. 1920
        screen.densityDpi = dm.densityDpi;// e.g. 480
        screen.density = dm.density;// e.g. 2.0
        return screen;
    }

    public static int getStatusBarHeight(Context context){

        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 38;//默认值

        try {

            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     * dp to px
     * @param dp
     * @return
     */
    public static int dp2px(Context context,int dp) {
        return (int) (getDensity(context) * dp + 0.5f);
    }

    /**
     * 获取density
     * @return
     */
    private static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * px to dp
     * @param px
     * @return
     */
    public static int px2dp(Context context,int px) {
        return (int) (getDensity(context) / px - 0.5f);
    }
}
