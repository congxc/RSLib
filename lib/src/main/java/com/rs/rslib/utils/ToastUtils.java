package com.rs.rslib.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast工具类
 */
public class ToastUtils {
    private static Toast mToast;
    private Context context;

    private ToastUtils(Context context){
        this.context = context;
    }



    /********************** 非连续弹出的Toast ***********************/
    public static void showSingleToast(Context context, int resId) { //R.string.**
        getSingleToast(context,resId, Toast.LENGTH_SHORT).show();
    }

    public static void showSingleToast(Context context, String text) {
        getSingleToast(context,text, Toast.LENGTH_SHORT).show();
    }

    public static void showSingleLongToast(Context context, int resId) {
        getSingleToast(context,resId, Toast.LENGTH_LONG).show();
    }

    public static void showSingleLongToast(Context context, String text) {
        getSingleToast(context,text, Toast.LENGTH_LONG).show();
    }

    /*********************** 连续弹出的Toast ************************/
    public static void showToast(Context context, int resId) {
        getToast(context,resId, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, String text) {
        getToast(context,text, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context context, int resId) {
        getToast(context,resId, Toast.LENGTH_LONG).show();
    }

    public static void showLongToast(Context context, String text) {
        getToast(context,text, Toast.LENGTH_LONG).show();
    }

    public static Toast getSingleToast(Context context, int resId, int duration) { // 连续调用不会连续弹出，只是替换文本
        return getSingleToast(context,context.getResources().getText(resId).toString(), duration);
    }

    public static Toast getSingleToast(Context context, String text, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context, text, duration);
        } else {
            mToast.setText(text);
        }
        return mToast;
    }

    public static Toast getToast(Context context, int resId, int duration) { // 连续调用会连续弹出
        return getToast(context,context.getResources().getText(resId).toString(), duration);
    }

    public static Toast getToast(Context context, String text, int duration) {
        return Toast.makeText(context, text, duration);
    }
}
