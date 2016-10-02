package com.rainbow.drawdemo.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rainbow.drawcore.utils.Constants;
import com.rainbow.drawcore.utils.FileUtils;
import com.rainbow.drawdemo.R;
import com.rainbow.drawdemo.utils.URLConfig;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/***
 * @author rainbow
 */
public class MainWebViewActivity extends Activity implements OnClickListener, View.OnTouchListener {

    private FrameLayout frameLayout;// 进度条
    private WebView mWebView;
    private LinearLayout empty_img_layout;
    private LinearLayout no_net_layout;
    private ImageView noNetIageView;
    private String url;
    private ImageView mScreenShotImageView;
    private long exitTime = 0;
    int screenWidth;
    int screenHeight;
    int lastX;
    int lastY;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initParam();
        initWebView();
        initLister();
    }


    private void initParam() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }

    /***
     * 初始化 webview
     */
    public void initWebView() {
        frameLayout = (FrameLayout) findViewById(R.id.ll_webview);
        empty_img_layout = (LinearLayout) findViewById( R.id.empty_image);
        no_net_layout = (LinearLayout) findViewById( R.id.no_net_image);
        noNetIageView = (ImageView) findViewById( R.id.no_net_img);
        mScreenShotImageView = (ImageView) findViewById( R.id.iv_sreen_shot);
        mWebView = (WebView) findViewById( R.id.mywebview);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setSavePassword(false);
        mWebView.getSettings().setSaveFormData(false);
        if (isNetworkAvailable(this)) {
            loadPage();
        } else {
            mWebView.setVisibility(View.GONE);
            frameLayout.setVisibility(View.GONE);
            empty_img_layout.setVisibility(View.GONE);
            no_net_layout.setVisibility(View.VISIBLE);
        }
        mWebView.setWebViewClient(client);
    }

    private void initLister() {

        noNetIageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Settings.ACTION_WIRELESS_SETTINGS), 1);
            }
        });
        mScreenShotImageView.setOnClickListener(this);
        mScreenShotImageView.setOnTouchListener(this);

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (url != null) {
            if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    if ((System.currentTimeMillis() - exitTime) > 2000) {
                        Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                        exitTime = System.currentTimeMillis();
                    } else {
                        finish();
                        System.exit(0);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    WebViewClient client = new WebViewClient() {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            MainWebViewActivity.this.url = url;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            MainWebViewActivity.this.url = url;
            frameLayout.setVisibility(View.GONE);

        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            view.stopLoading();
            mWebView.setVisibility(View.GONE);
            no_net_layout.setVisibility(View.GONE);
            empty_img_layout.setVisibility(View.VISIBLE);
            Toast.makeText(MainWebViewActivity.this, description, Toast.LENGTH_LONG).show();
        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (isNetworkAvailable(this.getApplicationContext())) {
                if (mWebView != null) {
                    loadPage();
                }
            }
        }
    }

    protected boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    private void loadPage() {
        mWebView.setVisibility(View.VISIBLE);
        frameLayout.setVisibility(View.VISIBLE);
        mWebView.loadUrl(URLConfig.HOME_PAGE);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.iv_sreen_shot:
                String bitmapPath = FileUtils.GetandSaveCurrentImage(this);
                PictrueDrawActivity.Jump(this, bitmapPath);
                break;

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
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
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        return false;
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

}
