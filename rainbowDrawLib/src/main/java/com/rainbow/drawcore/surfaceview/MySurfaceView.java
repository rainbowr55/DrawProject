package com.rainbow.drawcore.surfaceview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.rainbow.drawcore.shape.ALShape;
import com.rainbow.drawcore.shape.CircleShape;
import com.rainbow.drawcore.shape.LineShape;
import com.rainbow.drawcore.shape.PathShape;
import com.rainbow.drawcore.shape.Rectangle;
import com.rainbow.drawcore.shape.SurfaceShape;

import java.util.ArrayList;

/**
 * Created by rainbow on 16/3/1.
 */
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener,DrawOpretionCallBack {

    /**
     * 形状标识 //0=画矩形 1=画直线 2=画曲线 3=画圆形 4=画箭头 5=文字 6=正圆
     */
    public static final int SHAPE_CURVE = 2;
    public static final int SHAPE_LINE = 1;
    public static final int SHAPE_SQUARE = 0;
    public static final int SHAPE_OVAL = 3;
    public static final int SHAPE_ARROW = 4;
    public static final int SHAPE_TEXT = 5;
    public static final int SHAPE_CIRCLE = 6;

    /**
     * 当前形状
     */
    private int mShape = 3;
    private static final int NONE = 0;// 原始
    private static final int DRAG = 1;// 拖动
    private static final int ZOOM = 2;// 放大
    private int mStatus = NONE;

    /**
     * 缩放
     */
    private static final float MAX_ZOOM_SCALE = 4.0f;
    private static final float MIN_ZOOM_SCALE = 1.0f;
    private static final float FLOAT_TYPE = 1.0f;
    private float mCurrentMaxScale = MAX_ZOOM_SCALE;
    private float mCurrentScale = 1.0f;
    int mSurfaceHeight, mSurfaceWidth, mImageHeight, mImageWidth;
    float mCurrentImageHeight, mCurrentImageWidth;

    private PointF mStartPoint = new PointF();
    /**
     * 两个手指的中间点
     */
    private PointF midPoint = new PointF();
    private float mStartDistance = 0f;
    private float mNewDistance = 0f;

    private Context mContext;

    private SurfaceHolder mHolder = null;
    private Paint mPaint;
    private Bitmap mBitmap;
    private float startX;
    private float startY;
    private float stopX;
    private float stopY;

    private float lastX;
    private float lastY;
    private RectF currentRectF;
    private boolean isPaint = false;
    private boolean isClear = false;

    private SurfaceShape mCurrentShape;
    private int mCurrentPaintMode = 0;//0=新增一个形状 1选中一个形状的中心移动 2 选中圆形的边缘移动

    private ArrayList<SurfaceShape> mShapeList = new ArrayList<SurfaceShape>();
    /**
     * 用于记录拖拉图片移动的坐标位置
     */
    private Matrix matrix = new Matrix();
    /**
     * 用于记录图片要进行拖拉时候的坐标位置
     */
    private Matrix currentMatrix = new Matrix();

    int touche_mode = 0;
    float oldDistance = 0f;
    public int mCurrentColor = Color.parseColor("#ef3030");

    private EditTextHandleCallBack editTextHandleCallBack;

    public void setDrawCallBack(DrawCallBack drawCallBack) {
        this.drawCallBack = drawCallBack;
    }

    private DrawCallBack drawCallBack;

    public MySurfaceView(Context context, Bitmap bitmap) {
        super(context);
        mContext = context;
        mHolder = getHolder();
        mHolder.addCallback(this);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mCurrentColor);
        mBitmap = bitmap;
        mImageWidth = mBitmap.getWidth();
        mImageHeight = mBitmap.getHeight();
        mCurrentImageHeight = mImageHeight;
        mCurrentImageWidth = mImageWidth;
        this.setFocusable(true);
//        setZOrderOnTop(true);
        setOnTouchListener(this);
        init();
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("test", "surfaceCreated");
    }

    // 初始化
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Log.e("test", "surfaceChanged");
        synchronized (MySurfaceView.class) {
            mSurfaceHeight = height;
            mSurfaceWidth = width;
            init();
            if (mBitmap != null) {
                showBitmap();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e("test", "surfaceDestroyed");
    }

    private void init() {
        mCurrentMaxScale = Math.max(
                MIN_ZOOM_SCALE,
                4 * Math.min(FLOAT_TYPE * mImageHeight / mSurfaceHeight, 1.0f
                        * mImageWidth / mSurfaceWidth));
        mCurrentScale = MIN_ZOOM_SCALE;
        calcRect();

    }

    public void changeColor(int color){
        mCurrentColor = color;
        SurfaceShape shape = getSelectedShape();
        if(shape!=null){
            shape.changeColor(color);
        }
        paint();
    }
    private void paint() {
        Canvas canvas = mHolder.lockCanvas();
        canvas.concat(currentMatrix);
        currentMatrix = canvas.getMatrix();
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
        for (SurfaceShape shape : mShapeList) {
            shape.Draw(canvas, mPaint, 1.0f);
        }
        mHolder.unlockCanvasAndPost(canvas);
    }

    /**
     * 根据形状绘制
     *
     * @return
     */
    private SurfaceShape paintShape() {
        SurfaceShape shape = null;
        if (mShape == SHAPE_CIRCLE) {
            shape = new CircleShape(startX, startY, stopX, stopY);
        } else if (mShape == SHAPE_SQUARE) {
            shape = new Rectangle(startX, startY, stopX, stopY);
        } else if (mShape == SHAPE_OVAL) {
            shape = new Rectangle(startX, startY, stopX, stopY).setIsOval(true);
        } else if (mShape == SHAPE_LINE) {
            shape = new LineShape(startX, startY, stopX, stopY);
        }else if(mShape== SHAPE_ARROW){
            shape = new ALShape(startX,startY, stopX, stopY);
        }else if(mShape == SHAPE_CURVE){
            shape = new PathShape(startX,startY);
        }

        if(shape!=null){
            shape.changeColor(mCurrentColor);
        }
        return shape;
    }

    private void paintCurrentShape() {
        if (mCurrentShape != null) {
            Canvas canvas = mHolder.lockCanvas();
            mCurrentShape.startX = startX;
            mCurrentShape.startY = startY;
            mCurrentShape.stopX = stopX;
            mCurrentShape.stopY = stopY;
            mCurrentShape.changeColor(mCurrentColor);
            mCurrentShape.moveDraw(canvas, mPaint);
            mHolder.unlockCanvasAndPost(canvas);
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        float x = event.getX();
        float y = event.getY();
        isPaint = true;
        isClear = false;


        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                Log.e("test", "ACTION_DOWN");
                touche_mode = 0;
                mStartPoint.set(event.getX(), event.getY());
                touchBegan(x, y);

                mStatus = NONE;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.e("test", "ACTION_POINTER_DOWN");
                float distance = spacing(event);
                midPoint = mid(event);
                touche_mode = 2;
                mStartDistance = distance;
                oldDistance = distance;
                mStatus = DRAG;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e("test", "ACTION_MOVE");
                Log.e("test", "touch_mode=" + touche_mode);
                if (touche_mode >= 2) {
                    mNewDistance = spacing(event);
                    if (Math.abs(mNewDistance - oldDistance) > 10) {
                        mStatus = ZOOM;
                        oldDistance = mNewDistance;
                    } else {
                        mStatus = DRAG;
                    }
                } else {
                    mStatus = NONE;
                }
                if (mStatus == DRAG) {
                    dragAction(event);
                } else if (mStatus == ZOOM) {
                    zoomAcition(event);
                } else {
                    if (mStatus == NONE && event.getPointerCount() == 1 && touche_mode == 0) {
                        touchMoved(x, y);
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.e("test", "ACTION_UP");
                Log.e("test", "mStatus=" + mStatus + "touche_mode=" + touche_mode);
                if (mStatus != DRAG && mStatus != ZOOM && touche_mode <= 0) {
                    Log.e("test", "touchEnded");
                    touchEnded(x, y);
                }
                touche_mode = 0;
                mStatus = NONE;
            case MotionEvent.ACTION_POINTER_UP:
                Log.e("test", "ACTION_POINTER_UP");
                touche_mode = touche_mode - 1;
                mStatus = NONE;
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    public void clearSelect() {
        for (SurfaceShape shape : mShapeList) {
            shape.setSelect(false);
        }
    }
    public SurfaceShape getSelectedShape() {
        SurfaceShape shapeSelect=null;
        for (SurfaceShape shape : mShapeList) {
            if(shape.isSelect){
                shapeSelect = shape;
            }
        }
        return shapeSelect;
    }

    @Override
    public boolean deleteCurrentShape(){
        SurfaceShape shape = getSelectedShape();
        if(shape!=null){
            mShapeList.remove(shape);
            return true;
        }
        return false;
    }
    /**
     * 判断点击是否能够选中某个形状 后加的选中优先级比较高
     *
     * @param x x轴坐标位置
     * @param y y轴坐标位置
     * @return
     */
    private SurfaceShape getShapeByPosition(float x, float y) {
        mCurrentPaintMode = 0;
        int size = mShapeList.size();
        for (int i = size - 1; i >= 0; i--) {
            SurfaceShape shape = mShapeList.get(i);
            int state = shape.getState(x, y);
            //0在圆形外 1在圆形内选中移动并删除按钮出现 2 在圆形的边界
            if (state == 1 || state == 2) {
                //清除其他被选中的状态
                clearSelect();
                shape.setSelect(true);

                mCurrentPaintMode = state;
                Log.e("test", "mCurrentPaintMode=" + mCurrentPaintMode);
                return shape;
            }
        }
        return null;
    }


    private void touchBegan(float x, float y) {
        //0=新增一个形状 1选中一个形状的中心移动 2 选中圆形的边缘移动
        SurfaceShape shape = getShapeByPosition(x, y);
        startX = x;
        startY = y;
        lastX = x;
        lastY = y;
        stopX = x;
        stopY = y;
        //todo 判断edittext
        if (shape != null) {
            mCurrentShape = shape;
            currentRectF = mCurrentShape.getRectF();
        } else {
            clearSelect();
            mCurrentShape = paintShape();
            if(mCurrentShape==null){
                if (mShape == SHAPE_TEXT&&editTextHandleCallBack!=null) {
                    editTextHandleCallBack.addEditText(x,y);
                }
            }
        }
    }

    private void touchMoved(float x, float y) {
        stopX = x;
        stopY = y;
        isPaint = false;
        if (Math.abs(stopX - lastX) > 70f || Math.abs(stopY - lastY) > 70f||mShape==SHAPE_CURVE) {
            isPaint = true;
            if (mCurrentPaintMode == 0) {//新增
                paint();
                paintCurrentShape();
                lastX = x;
                lastY = y;
            } else if (mCurrentPaintMode == 1) {//移动位置
                if (mCurrentShape != null) {
                    float dx = Math.abs(x - startX);
                    float dy = Math.abs(y - startY);
                    if (dx > 0 || dy > 0) {
                        mCurrentShape.changPosition(currentRectF, x, y, startX, startY);
                    }
                }
                paint();
            } else if (mCurrentPaintMode == 2) {//修改形状
                mCurrentShape.changeShape(x, y);
                paint();
            }

        }

    }

    private void touchEnded(float x, float y) {
        switch (mShape) {
            case SHAPE_CURVE:
            case SHAPE_LINE:
            case SHAPE_CIRCLE:
            case SHAPE_SQUARE:
            case SHAPE_OVAL:
            case SHAPE_ARROW:
                stopX = x;
                stopY = y;
                if (mCurrentPaintMode == 0) {
                    if (mCurrentShape != null && isPaint) {
                        if (Math.abs(mCurrentShape.startX - mCurrentShape.stopX) > 70f || Math.abs(mCurrentShape.startY - mCurrentShape.stopY) > 70f) {
                            clearSelect();
                            mShapeList.add(mCurrentShape);
                            mCurrentShape.setSelect(true);
                            if(drawCallBack!=null){
                                drawCallBack.onSelectShape();
                            }
                        }else{
                            if(drawCallBack!=null){
                                drawCallBack.onClearSelect();
                            }
                        }
                    }else{
                        if(drawCallBack!=null){
                            drawCallBack.onClearSelect();
                        }
                    }
                }else{
                    if(drawCallBack!=null){
                        drawCallBack.onSelectShape();
                    }
                }
                paint();
                break;
        }
    }

    public void setShape(int shape) {
        mShape = shape;
        isPaint = false;
    }

    public void clear() {
//        if (mBitmap != null) {
//            mCanvas = mHolder.lockCanvas();
//            mBitmap = Bitmap.createBitmap(DisplayUtil.getScreenWidth(mContext),
//                    DisplayUtil.getScreenHeight(mContext), Bitmap.Config.ARGB_8888);
//            mCanvas.setBitmap(mBitmap);
//            mHolder.unlockCanvasAndPost(mCanvas);
//        }
//        if (mPath != null) {
//            mPath.reset();
//        }
//        isClear = true;
//        invalidate();
    }

    private void calcRect() {
        int w, h;
        float imageRatio, surfaceRatio;
        imageRatio = FLOAT_TYPE * mImageWidth / mImageHeight;
        surfaceRatio = FLOAT_TYPE * mSurfaceWidth / mSurfaceHeight;

        if (imageRatio < surfaceRatio) {
            h = mSurfaceHeight;
            w = (int) (h * imageRatio);
        } else {
            w = mSurfaceWidth;
            h = (int) (w / imageRatio);
        }

        if (mCurrentScale > MIN_ZOOM_SCALE) {
            w = Math.min(mSurfaceWidth, (int) (w * mCurrentScale));
            h = Math.min(mSurfaceHeight, (int) (h * mCurrentScale));
        } else {
            mCurrentScale = MIN_ZOOM_SCALE;
        }

    }

    public void setMaxZoom(float value) {
        mCurrentMaxScale = value;
    }

    public void setBitmap(Bitmap b) {

        if (b == null) {
            return;
        }
        synchronized (MySurfaceView.class) {
            mBitmap = b;
            if (mImageHeight != mBitmap.getHeight()
                    || mImageWidth != mBitmap.getWidth()) {
                mImageHeight = mBitmap.getHeight();
                mImageWidth = mBitmap.getWidth();
                init();
            }
            showBitmap();
        }

    }

    private void showBitmap() {
        synchronized (MySurfaceView.class) {
            paint();
        }
    }

    private void dragAction(MotionEvent event) {

        synchronized (MySurfaceView.class) {
            PointF currentPoint = new PointF();
            currentPoint.set(event.getX(), event.getY());
            float dx = event.getX() - mStartPoint.x;
            // 得到x轴的移动距离
            float dy = event.getY() - mStartPoint.y;
            mStartPoint = currentPoint;
            // 得到x轴的移动距离
            Log.e("test", "dx=" + dx + ",dy=" + dy);
            matrix.set(currentMatrix);
            midPoint.x = midPoint.x - dx;
            midPoint.y = midPoint.y - dy;
            matrix.setScale(mCurrentScale, mCurrentScale, midPoint.x, midPoint.y);
            currentMatrix.set(matrix);
            showBitmap();
        }

    }

    private void zoomAcition(MotionEvent event) {
        Log.e("test", "zoomAcition");
        synchronized (MySurfaceView.class) {
            float newDist = spacing(event);
            float scale = newDist / mStartDistance;
            mStartDistance = newDist;
            mCurrentScale *= scale;
            mCurrentScale = Math.max(FLOAT_TYPE,
                    Math.min(mCurrentScale, mCurrentMaxScale));
            matrix.set(currentMatrix);
            matrix.setScale(mCurrentScale, mCurrentScale, midPoint.x, midPoint.y);
            currentMatrix.set(matrix);
            showBitmap();
        }

    }


    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 计算两个手指间的中间点
     */
    private PointF mid(MotionEvent event) {
        float midX = (event.getX(1) + event.getX(0)) / 2;
        float midY = (event.getY(1) + event.getY(0)) / 2;
        return new PointF(midX, midY);
    }

    public void setEditTextHandleCallBack(EditTextHandleCallBack callBack){
        this.editTextHandleCallBack = callBack;
    }
}
