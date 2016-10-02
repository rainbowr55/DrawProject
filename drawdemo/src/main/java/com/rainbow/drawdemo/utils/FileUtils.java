package com.rainbow.drawdemo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;


import com.rainbow.drawcore.utils.BDebug;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by liangcaihong on 2016/1/21.
 */
public class FileUtils {
    public static void writeToSDCard(String fileName, InputStream input) {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File directory = Environment.getExternalStorageDirectory();
            File file = new File(directory, fileName);
            try {
                FileOutputStream fos = new FileOutputStream(file);
                byte[] b = new byte[2048];
                int j = 0;
                while ((j = input.read(b)) != -1) {
                    fos.write(b, 0, j);
                }
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            BDebug.i("tag", "NO SDCard.");
        }
    }

    public static String getMIMEType(File f) {
        String type = "";
        String fName = f.getName();
        /* 取得扩展名 */
        String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();

        /* 依扩展名的类型决定MimeType */
        if (end.equals("apk")) {
            /* android.permission.INSTALL_PACKAGES */
            type = "application/vnd.android.package-archive";
        } else {
            // /*如果无法直接打开，就跳出软件列表给用户选择 */
            type = "*/*";
        }
        return type;
    }

    public static Intent getFileIntent(File file) {
        Uri uri = Uri.fromFile(file);
        String type = FileUtils.getMIMEType(file);
        BDebug.i("tag", "type=" + type);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uri, type);
        return intent;
    }

    public static String getCurrentImageByView(View v,Context context){
        Bitmap Bmp = getViewBitmap(v);
        //3.保存Bitmap
        File path = getDiskCacheDir(context, 1);
        //文件
        String fileName = "splunk_screen_" + System.currentTimeMillis() + ".png";
        File file = new File(path, fileName);
        try {
            if (path != null && !path.exists()) {
                path.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            if (null != fos) {
                Bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        BDebug.e("test", "save path:" + file.getAbsolutePath());
        return file.getAbsolutePath();
    }

    // 将模板View的图片转化为Bitmap
    public static Bitmap getBitmapByView(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    /**

     * 根据view来生成bitmap图片，可用于截图功能

     */

    public static Bitmap getViewBitmap(View v) {

        v.clearFocus(); //

        v.setPressed(false); //

        // 能画缓存就返回false

        boolean willNotCache = v.willNotCacheDrawing();

        v.setWillNotCacheDrawing(false);

        int color = v.getDrawingCacheBackgroundColor();

        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {

            v.destroyDrawingCache();

        }

        v.buildDrawingCache();

        Bitmap cacheBitmap = v.getDrawingCache();

        if (cacheBitmap == null) {

            return null;

        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view

        v.destroyDrawingCache();

        v.setWillNotCacheDrawing(willNotCache);

        v.setDrawingCacheBackgroundColor(color);

        return bitmap;

    }

    // 获取指定Activity的截屏，保存到png文件 去掉状态栏
    private static Bitmap takeScreenShot(Activity activity) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        Log.i("TAG", "" + statusBarHeight);
        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay()
                .getHeight();
        // 去掉标题栏
        // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }
    public static String GetandSaveCurrentImage(Activity context) {
//        //1.构建Bitmap
//        WindowManager windowManager = context.getWindowManager();
//        Display display = windowManager.getDefaultDisplay();
//        int w = display.getWidth();
//        int h = display.getHeight();
////        Bitmap Bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
//        //2.获取屏幕
//        View decorview = context.getWindow().getDecorView();
//        decorview.setDrawingCacheEnabled(true);
//        Bitmap Bmp  = decorview.getDrawingCache();

        Bitmap Bmp = takeScreenShot(context);
        //3.保存Bitmap
        File path = getDiskCacheDir(context, 1);
        //文件
        String fileName = "splunk_screen_" + System.currentTimeMillis() + ".png";
        File file = new File(path, fileName);
        try {
            if (path != null && !path.exists()) {
                path.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            if (null != fos) {
                Bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        BDebug.e("test","save path:"+file.getAbsolutePath());
        return file.getAbsolutePath();
    }

    public static File getDiskCacheDir(Context context, int showMethod) {
        File cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            if (showMethod == 0) {
                cachePath = context.getExternalCacheDir();
            } else {
                cachePath = Environment.getExternalStoragePublicDirectory("splunk");
                if (!cachePath.exists()) {
                    cachePath.mkdir();
                }
            }

        } else {
            if (showMethod == 0) {
                cachePath = context.getCacheDir();
            } else {
                cachePath = Environment.getRootDirectory();
            }

        }
        return cachePath;
    }
}
