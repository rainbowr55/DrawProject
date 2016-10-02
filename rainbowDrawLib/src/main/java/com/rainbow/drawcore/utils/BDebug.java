package com.rainbow.drawcore.utils;

import android.text.TextUtils;
import android.util.Log;


public class BDebug {
    public static boolean IsDebug = true;
    public static void log(String msg) {
        if (IsDebug && !TextUtils.isEmpty(msg)) {
            System.out.println(msg);
        }
    }

    public static void e(String tag, String msg) {
        if (IsDebug && !TextUtils.isEmpty(msg)) {
            Log.e(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (IsDebug && !TextUtils.isEmpty(msg)) {
            Log.d(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (IsDebug && !TextUtils.isEmpty(msg)) {
            Log.v(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (IsDebug&& !TextUtils.isEmpty(msg)) {
            Log.w(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (IsDebug && !TextUtils.isEmpty(msg)) {
            Log.i(tag, msg);
        }
    }
}
