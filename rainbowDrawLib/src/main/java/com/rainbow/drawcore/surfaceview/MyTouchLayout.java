package com.rainbow.drawcore.surfaceview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.rainbow.drawcore.R;
import com.rainbow.drawcore.utils.Utils;

/**
 * Created by rainbow on 16/8/11.
 */
public class MyTouchLayout extends RelativeLayout implements Component {


    private Context mContext;
    int screenWidth;
    int screenHeight;
    int lastX;
    int lastY;

    public MyTouchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels - 50;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View v = this;
        RelativeLayout.LayoutParams st =
                (RelativeLayout.LayoutParams) v.getLayoutParams();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) event.getRawX() - lastX;
                int dy = (int) event.getRawY() - lastY;

                int left = v.getLeft() + dx;
                int top = v.getTop() + dy;
                int right = v.getRight() + dx;
                int bottom = v.getBottom() + dy;
                if (left < 0) {
                    left = 0;
                    right = left + v.getWidth();
                }
                if (right > screenWidth) {
                    right = screenWidth;
                    left = right - v.getWidth();
                }
                if (top < 0) {
                    top = 0;
                    bottom = top + v.getHeight();
                }
                if (bottom > screenHeight) {
                    bottom = screenHeight;
                    top = bottom - v.getHeight();
                }
                v.layout(left, top, right, bottom);
//                st.topMargin = (int) v.getY();
//                st.leftMargin = (int) v.getX();
                st.topMargin = top;
                st.leftMargin = left;
                st.bottomMargin = bottom;
                st.rightMargin = right;
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                v.setLayoutParams(st);
                break;

        }
        // 事件分发交给父类
        super.dispatchTouchEvent(event);
        return true;
    }

    @Override
    public void select() {

    }

    @Override
    public void remove() {
        if (this.getParent() != null && this.getParent() instanceof RelativeLayout && this != null) {
            RelativeLayout parent = (RelativeLayout) this.getParent();
            if (parent != null) {
                EditText myEditText = (EditText) this.findViewById(R.id.add_edit_text);
                if (myEditText != null) {
                    Utils.hideSoftKeyboardNotClear(mContext, myEditText);
                }
                parent.removeView(this);
            }

        }
    }

    @Override
    public boolean selfClear() {
        EditText myEditText = (EditText) this.findViewById(R.id.add_edit_text);
        if (myEditText != null) {
            if (TextUtils.isEmpty(myEditText.getText().toString())) {
                if (TextUtils.isEmpty(myEditText.getText().toString().trim())) {
                    remove();
                    return true;
                } else {
                    myEditText.clearFocus();
                    Utils.hideSoftKeyboardNotClear(mContext, myEditText);
                }
            }
        }
        return false;
    }

    @Override
    public int getType() {
        return 1;
    }


}
