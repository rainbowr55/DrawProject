package com.rainbow.drawdemo.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 数据转换工具类
 */
public class ConvertUtils {

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(float spValue, Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return (int) (spValue * dm.scaledDensity + 0.5f);
    }

    public static int px2sp(float pxValue, Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return (int) ((pxValue - 0.5f) / dm.scaledDensity);
    }

    public static int convertDpToPixel(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    /**
     * 让输入框获取焦点，并弹出键盘
     *
     * @param context
     * @param editText
     */
    public static void showSoftKeyboard(Context context, final EditText editText) {
        if (null == context || null == editText)
            return;
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager inputManager =
                        (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);
            }
        }, 300);
    }

}
