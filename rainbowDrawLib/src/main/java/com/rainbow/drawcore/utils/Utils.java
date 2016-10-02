package com.rainbow.drawcore.utils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by rainbow on 16/8/11.
 */
public class Utils {

    /**
     * 设置 EditText="" , 清除选中的焦点 隐藏键盘
     *
     * @param context
     * @param editText
     */
    public static void hideSoftKeyboard(Context context, EditText editText) {
        // editText.setText("");
        editText.clearFocus();
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * 清除选中的焦点 隐藏键盘 但不清除文字
     *
     * @param context
     * @param editText
     */
    public static void hideSoftKeyboardNotClear(Context context, EditText editText) {
        editText.clearFocus();
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
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
