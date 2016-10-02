package com.rainbow.drawdemo.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rainbow.drawcore.surfaceview.Component;
import com.rainbow.drawcore.surfaceview.DrawCallBack;

import com.rainbow.drawcore.surfaceview.*;

import com.rainbow.drawcore.utils.FileUtils;
import com.rainbow.drawdemo.R;
import com.rainbow.drawdemo.utils.Constants;
import com.rainbow.drawdemo.utils.ConvertUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by rainbow on 16/3/4.
 */
public class PictrueDrawActivity extends Activity implements View.OnClickListener,EditTextHandleCallBack,DrawCallBack {
    public static final ReadWriteLock lock = new ReentrantReadWriteLock(false);
    private String mPath = null;
    private RelativeLayout mImageContentLayout;
    private MySurfaceView mImageScaleImage;
    private RelativeLayout mImageEditMainLayout;

    private ImageView mDeleteShape;
    private TextView mTopClose, mTopCancle, mTopResume, mTopClear, mTopShare;
    private ImageView mColorSelectSwitchTab, mArrawSelectSwitchTab, mShapeSelectSwitchTab, mTextSelectSwitchTab;
    private ImageView mColorSelectSwitch, mArrawSelectSwitch, mShapeSelectSwitch, mTextSelectSwitch;
    private boolean isTools = false;

    private RelativeLayout mColorPannelLayout, mShapePannelLayout;
    private Animation mViewShowAnimation, mViewHideAnimation = null;
    private ImageView mColorIv1, mColorIv2, mColorIv3, mColorIv4, mColorIv5, mColorIv6, mColorIv7, mColorIv8;

    private ImageView mRadioButton1, mRadioButton2, mRadioButton3, mRadioButton4;

    private LayoutInflater mLayoutInflater;

    private ArrayList<Component> draws = new ArrayList<Component>();


    private String mImagePath;
    Bitmap mBitmap;
    /**
     * //0=画矩形 1=画直线 2=画曲线 3=画圆形 4=画箭头 5=文字
     */
    private int mEditType = 0;
    /**
     * 橙色＃ff7200    紫色＃9943e3    青色＃24def4    绿色＃07df8b   蓝色＃288fe7         黄色＃ffe404    红色＃ef3030    黑色＃000000
     */
    public static String mCurrenColor = "#ef3030";

    private int statusBarHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pictrue_draw_main);
        initParam();
        initView();
        fillContent();
    }

    public static void Jump(Context context, String imagePath) {
        Intent intent = new Intent(context,
                PictrueDrawActivity.class);
        intent.putExtra("camera_path", imagePath);
        context.startActivity(intent);
    }

    private void initParam() {
        Intent intent = getIntent();
        mImagePath = intent.getStringExtra("camera_path");
        mBitmap = BitmapFactory.decodeFile(mImagePath);
        mLayoutInflater = this.getLayoutInflater();
    }

    private void initView() {
        mImageEditMainLayout = (RelativeLayout) findViewById(R.id.splunk_image_edit_main);
        mImageContentLayout = (RelativeLayout) findViewById(R.id.splunk_image_layout);
        mDeleteShape = (ImageView) findViewById(R.id.draw_delete_shape);
        mDeleteShape.setOnClickListener(this);
        initBottomToolView(mImageEditMainLayout);
        initTopToolView(mImageEditMainLayout);
    }


    private void fillContent() {
        Bitmap resizeBmp = BitmapFactory.decodeFile(mImagePath);
        if (mImageScaleImage != null) {
            mImageContentLayout.removeAllViews();
        }
        mImageScaleImage = new MySurfaceView(this, resizeBmp);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        mImageContentLayout.addView(mImageScaleImage, mImageContentLayout.getChildCount() - 1);
        mImageScaleImage.setEditTextHandleCallBack(this);
        mImageScaleImage.setDrawCallBack(this);

    }

    private void initTopToolView(View topView) {
        mTopClose = (TextView) topView.findViewById(R.id.btn_close);
        mTopShare = (TextView) topView.findViewById(R.id.btn_share);
        mTopCancle = (TextView) topView.findViewById(R.id.btn_cancel);
        mTopResume = (TextView) topView.findViewById(R.id.btn_resume);
        mTopClear = (TextView) topView.findViewById(R.id.btn_clear);
        mTopCancle.setOnClickListener(this);
        mTopResume.setOnClickListener(this);
        mTopClear.setOnClickListener(this);
        mTopClose.setOnClickListener(this);
        mTopShare.setOnClickListener(this);
    }

    public void initBottomToolView(View v) {
        mColorSelectSwitch = (ImageView) v.findViewById(R.id.iv_select_color);
        mShapeSelectSwitch = (ImageView) v.findViewById(R.id.iv_select_shape);
        mArrawSelectSwitch = (ImageView) v.findViewById(R.id.iv_select_arrow);
        mTextSelectSwitch = (ImageView) v.findViewById(R.id.iv_select_text);
        mColorSelectSwitchTab = (ImageView) v.findViewById(R.id.iv_select_color_tab);
        mShapeSelectSwitchTab = (ImageView) v.findViewById(R.id.iv_select_shape_tab);
        mArrawSelectSwitchTab = (ImageView) v.findViewById(R.id.iv_select_arrow_tab);
        mTextSelectSwitchTab = (ImageView) v.findViewById(R.id.iv_select_text_tab);
        mColorPannelLayout = (RelativeLayout) v.findViewById(R.id.select_color_pannel_layout);
        mShapePannelLayout = (RelativeLayout) v.findViewById(R.id.select_shape_pannel_layout);
        mColorIv1 = (ImageView) v.findViewById(R.id.iv_color_black);
        mColorIv2 = (ImageView) v.findViewById(R.id.iv_color_red);
        mColorIv3 = (ImageView) v.findViewById(R.id.iv_color_orange);
        mColorIv4 = (ImageView) v.findViewById(R.id.iv_color_yellow);
        mColorIv5 = (ImageView) v.findViewById(R.id.iv_color_green);
        mColorIv6 = (ImageView) v.findViewById(R.id.iv_color_blue);
        mColorIv7 = (ImageView) v.findViewById(R.id.iv_color_cyan);
        mColorIv8 = (ImageView) v.findViewById(R.id.iv_color_purple);

        mRadioButton1 = (ImageView) v.findViewById(R.id.iv_shape_line);
        mRadioButton2 = (ImageView) v.findViewById(R.id.iv_shape_curve);
        mRadioButton3 = (ImageView) v.findViewById(R.id.iv_shape_rectangle);
        mRadioButton3.setSelected(true);//默认选中矩形
        mRadioButton4 = (ImageView) v.findViewById(R.id.iv_shape_round);

        mColorSelectSwitch.setOnClickListener(this);
        mShapeSelectSwitch.setOnClickListener(this);
        mColorIv1.setOnClickListener(this);
        mColorIv2.setOnClickListener(this);
        mColorIv3.setOnClickListener(this);
        mColorIv4.setOnClickListener(this);
        mColorIv5.setOnClickListener(this);
        mColorIv6.setOnClickListener(this);
        mColorIv7.setOnClickListener(this);
        mColorIv8.setOnClickListener(this);
        mRadioButton1.setOnClickListener(this);
        mRadioButton2.setOnClickListener(this);
        mRadioButton3.setOnClickListener(this);
        mRadioButton4.setOnClickListener(this);
        mArrawSelectSwitch.setOnClickListener(this);
        mTextSelectSwitch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_close:
                this.finish();
                break;
            case R.id.iv_color_black:
                ImageView imageView1 = (ImageView) v;
                mColorSelectSwitch.setImageDrawable(imageView1.getDrawable());
                hideColorPannelBottom();
                mCurrenColor = "#000000";
                break;
            case R.id.iv_color_red:
                ImageView imageView2 = (ImageView) v;
                mColorSelectSwitch.setImageDrawable(imageView2.getDrawable());
                hideColorPannelBottom();
                mCurrenColor = "#ef3030";
                break;
            case R.id.iv_color_yellow:
                ImageView imageView3 = (ImageView) v;
                mColorSelectSwitch.setImageDrawable(imageView3.getDrawable());
                hideColorPannelBottom();
                mCurrenColor = "#ffe404";
                break;
            case R.id.iv_color_green:
                ImageView imageView4 = (ImageView) v;
                mColorSelectSwitch.setImageDrawable(imageView4.getDrawable());
                hideColorPannelBottom();
                mCurrenColor = "#07df8b";
                break;
            case R.id.iv_color_cyan:
                ImageView imageView = (ImageView) v;
                mColorSelectSwitch.setImageDrawable(imageView.getDrawable());
                hideColorPannelBottom();
                mCurrenColor = "#24def4";
                break;
            case R.id.iv_color_purple:
                ImageView imageView5 = (ImageView) v;
                mColorSelectSwitch.setImageDrawable(imageView5.getDrawable());
                hideColorPannelBottom();
                mCurrenColor = "#9943e3";
                break;
            case R.id.iv_color_orange:
                ImageView imageView6 = (ImageView) v;
                mColorSelectSwitch.setImageDrawable(imageView6.getDrawable());
                hideColorPannelBottom();
                mCurrenColor = "#ff7200";
                break;
            case R.id.iv_color_blue:
                ImageView imageView7 = (ImageView) v;
                mColorSelectSwitch.setImageDrawable(imageView7.getDrawable());
                hideColorPannelBottom();
                mCurrenColor = "#9943e3";
                break;
            case R.id.iv_shape_line:
                mShapeSelectSwitch.setImageDrawable(mRadioButton1.getDrawable());
                mShapeSelectSwitch.setSelected(true);
                handleRadioButton(mRadioButton1);
                mEditType = 1;//0=画矩形 1=画直线 2=画曲线 3=画圆形 4=画箭头 5=文字
                break;
            case R.id.iv_shape_curve:
                mShapeSelectSwitch.setImageDrawable(mRadioButton2.getDrawable());
                mShapeSelectSwitch.setSelected(true);
                handleRadioButton(mRadioButton2);
                mEditType = 2;//0=画矩形 1=画直线 2=画曲线 3=画圆形 4=画箭头 5=文字
                break;
            case R.id.iv_shape_rectangle:
                mShapeSelectSwitch.setImageDrawable(mRadioButton3.getDrawable());
                mShapeSelectSwitch.setSelected(true);
                handleRadioButton(mRadioButton3);
                mEditType = 0;//0=画矩形 1=画直线 2=画曲线 3=画圆形 4=画箭头 5=文字
                break;
            case R.id.iv_shape_round:
                mShapeSelectSwitch.setImageDrawable(mRadioButton4.getDrawable());
                mShapeSelectSwitch.setSelected(true);
                handleRadioButton(mRadioButton4);
                mEditType = 3;//0=画矩形 1=画直线 2=画曲线 3=画圆形 4=画箭头 5=文字
                break;
            case R.id.iv_select_color:
                hideShapePannelBottom();
                if (mColorPannelLayout.getVisibility() != View.GONE) {
                    hideColorPannelBottom();
                } else if (mColorPannelLayout.getVisibility() == View.GONE) {
                    showColorPannel();
                }
                mColorSelectSwitchTab.setSelected(true);
                handleSelectSwitch(R.id.iv_select_color);
                clearEditTextFocus();
                break;
            case R.id.iv_select_arrow:
                mArrawSelectSwitch.setSelected(true);
                mArrawSelectSwitchTab.setSelected(true);
                handleSelectSwitch(R.id.iv_select_arrow);
                mEditType = 4;//0=画矩形 1=画直线 2=画曲线 3=画圆形 4=画箭头 5=文字
                clearEditTextFocus();
                break;
            case R.id.iv_select_shape:
                mShapeSelectSwitch.setSelected(true);
                mShapeSelectSwitchTab.setSelected(true);
                handleSelectSwitch(R.id.iv_select_shape);
                if (mShapePannelLayout.getVisibility() != View.GONE) {
                    hideShapePannelBottom();
                } else if (mShapePannelLayout.getVisibility() == View.GONE) {
                    showShapePannel();
                }
                clearEditTextFocus();
                break;
            case R.id.iv_select_text:
                mTextSelectSwitch.setSelected(true);
                mTextSelectSwitchTab.setSelected(true);
                handleSelectSwitch(R.id.iv_select_text);
                mEditType = 5;//0=画矩形 1=画直线 2=画曲线 3=画圆形 4=画箭头 5=文字
                break;
            case R.id.draw_delete_shape://隐藏的删除按钮
                //删除当前形状
                deleteCurrentSelectShape();
                break;

            case R.id.btn_share:
                clearEditTextFocus();
                btnSave();
                break;
            case R.id.btn_cancel:
                //// TODO: 16/3/10 撤销功能 
                break;
            case R.id.btn_resume:
                //// TODO: 16/3/10 重做功能
                break;
        }
        setShape(mEditType);
        mImageScaleImage.changeColor(Color.parseColor(mCurrenColor));
    }

    private void deleteCurrentSelectShape(){
        if(mImageScaleImage.deleteCurrentShape()){
            mDeleteShape.setVisibility(View.GONE);
        }
    }



    private void clearEditTextFocus() {
        lock.readLock().lock();
        if (draws != null) {
            int size = draws.size();
            for (Iterator<Component> it = draws.iterator(); it.hasNext(); ) {
                Component s = it.next();
                if (s.getType() == 1) {
                    if (s.selfClear()) {
                        it.remove();
                    }
                }
            }
        }
        lock.readLock().unlock();
    }

    public void setShape(int shape) {
        if (mImageScaleImage != null && shape >= 0 && shape <= 5) {
            mImageScaleImage.setShape(shape);
        }
    }

    private void handleSelectSwitch(int id) {

        if (id != R.id.iv_select_arrow) {
            mArrawSelectSwitch.setSelected(false);
            mArrawSelectSwitchTab.setSelected(false);
        }
        if (id != R.id.iv_select_shape) {
            mShapeSelectSwitch.setSelected(false);
            mShapeSelectSwitchTab.setSelected(false);
            hideShapePannelBottom();
        }
        if (id != R.id.iv_select_text) {
            mTextSelectSwitch.setSelected(false);
            mTextSelectSwitchTab.setSelected(false);
        }
        if(id!= R.id.iv_select_color){
            mColorPannelLayout.setVisibility(View.GONE);
            mColorSelectSwitchTab.setSelected(false);
        }
    }

    private void handleRadioButton(ImageView radioButton) {
        radioButton.setSelected(true);
        clearRadioCheck(radioButton.getId());
    }

    private void clearRadioCheck(int id) {
        if (id != R.id.iv_shape_line) {
            mRadioButton1.setSelected(false);
        }
        if (id != R.id.iv_shape_curve) {
            mRadioButton2.setSelected(false);
        }
        if (id != R.id.iv_shape_rectangle) {
            mRadioButton3.setSelected(false);
        }
        if (id != R.id.iv_shape_round) {
            mRadioButton4.setSelected(false);
        }


    }

    /**
     * 显示颜色面板
     */
    private void showColorPannel() {
        mColorPannelLayout.setVisibility(View.VISIBLE);
        if (mViewShowAnimation == null) {
            mViewShowAnimation = new AlphaAnimation(0, 1.0f);
            mViewShowAnimation.setDuration(200);
        }
        mColorPannelLayout.startAnimation(mViewShowAnimation);
    }

    /**
     * 隐藏颜色面板
     */
    private void hideColorPannelBottom() {
        mColorPannelLayout.setVisibility(View.GONE);
        if (mViewHideAnimation == null) {
            mViewHideAnimation = new AlphaAnimation(1.0f, 0);
            mViewHideAnimation.setDuration(200);
        }
        mColorPannelLayout.startAnimation(mViewHideAnimation);
    }

    /**
     * 显示形状面板
     */
    private void showShapePannel() {
        mShapePannelLayout.setVisibility(View.VISIBLE);
        if (mViewShowAnimation == null) {
            mViewShowAnimation = new AlphaAnimation(0, 1.0f);
            mViewShowAnimation.setDuration(200);
        }
        mShapePannelLayout.startAnimation(mViewShowAnimation);
    }


    private void btnSave() {
        Bitmap bmp = FileUtils.getBitmapByView(mImageContentLayout);
        if (bmp != null) {
            mPath = saveBitmap(bmp, "saveTemp");
            Intent okData = new Intent();
            okData.putExtra("camera_path", mPath);
            setResult(RESULT_OK, okData);
        }
    }


    // 将生成的图片保存到内存中
    public String saveBitmap(Bitmap bitmap, String name) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File dir = new File(Constants.filePath);
            if (!dir.exists())
                dir.mkdir();
            File file = new File(Constants.filePath + name + ".jpg");
            FileOutputStream out;

            try {
                out = new FileOutputStream(file);
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                    out.flush();
                    out.close();
                }
                return file.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * 隐藏形状面板
     */
    private void hideShapePannelBottom() {
        if (mShapePannelLayout.getVisibility() == View.GONE) {
            return;
        }
        mShapePannelLayout.setVisibility(View.GONE);
        if (mViewHideAnimation == null) {
            mViewHideAnimation = new AlphaAnimation(1.0f, 0);
            mViewHideAnimation.setDuration(200);
        }
        mShapePannelLayout.startAnimation(mViewHideAnimation);
    }

    private boolean measure = false;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && !measure) {
            measure = true;
            Rect frame = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            int statusBarHeight = frame.top;
            if (0 == statusBarHeight) {
                Class<?> localClass;
                try {
                    localClass = Class.forName("com.android.internal.R$dimen");
                    Object localObject = localClass.newInstance();
                    int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                    statusBarHeight = this.getResources().getDimensionPixelSize(i5);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }

            this.statusBarHeight = statusBarHeight;
        }
    }




    @Override
    public void addEditText(float x, float y) {
        final MyTouchLayout editLayout = (MyTouchLayout) mLayoutInflater.inflate(R.layout.edit_text_layout, null);
        // EditText editLayout = (EditText) mLayoutInflater.inflate(R.layout.edit_text_float_layout, null);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.topMargin = (int) y;
        lp.leftMargin = (int) x;
        final EditText et = (EditText) editLayout.findViewById(R.id.add_edit_text);
        ImageView remove = (ImageView) editLayout.findViewById(R.id.delete_edit_text);
        et.setTextColor(Color.parseColor(PictrueDrawActivity.mCurrenColor));
        et.setLongClickable(false);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editLayout != null) {
                    editLayout.remove();
                    draws.remove(editLayout);
                }

            }
        });
        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    clearEditText(editLayout);
                }
            }
        });
        draws.add(editLayout);
        mImageContentLayout.addView(editLayout, lp);
        ConvertUtils.showSoftKeyboard(this, et);
    }

    @Override
    public void clearEditText(MyTouchLayout et) {
        lock.readLock().lock();
        if (draws != null) {
            int size = draws.size();
            for (Iterator<Component> it = draws.iterator(); it.hasNext(); ) {
                Component s = it.next();
                if (s.getType() == 1 && !s.equals(et)) {
                    if (s.selfClear()) {
                        it.remove();
                    }
                }
            }
        }
        lock.readLock().unlock();
    }

    @Override
    public void onSelectShape() {
        mDeleteShape.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClearSelect() {
        mDeleteShape.setVisibility(View.GONE);
    }
//
//    private void clearEditTextFocus() {
//        lock.readLock().lock();
//        if (draws != null) {
//            int size = draws.size();
//            for (Iterator<Component> it = draws.iterator(); it.hasNext(); ) {
//                Component s = it.next();
//                if (s.getType() == 1) {
//                    if (s.selfClear()) {
//                        it.remove();
//                    }
//                }
//            }
//        }
//        lock.readLock().unlock();
//    }

}
