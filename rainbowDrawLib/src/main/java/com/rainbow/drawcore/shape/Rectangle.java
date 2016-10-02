package com.rainbow.drawcore.shape;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by rainbow on 16/3/2.
 */
public class Rectangle extends SurfaceShape {

    public float left;
    public float top;

    public float right;
    public float bottom;
    private static final int LEFT_TOP = 1;
    private static final int RIGHT_TOP = 2;

    private static final int LEFT_BOTTOM = 3;
    private static final int RIGHT_BOTTOM = 4;
    private static int currentPoint = 0;
    private boolean isOval = false;

    public Rectangle setIsOval(boolean isOval) {
        this.isOval = isOval;
        return this;
    }

    public Rectangle(float startX, float startY, float stopX, float stopY) {
        super(startX, startY, stopX, stopY);
    }

    @Override
    public SurfaceShape Draw(Canvas canvas, Paint paint,float scale) {
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(width);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        width =  LINE_WIDTH*(int)scale;
        float horistal = (Math.abs(left-right)*(scale-1.0f)/2);
        float vertal = Math.abs(top-bottom)*(scale-1.0f)/2;
        float l= left-horistal;
        float r = right+horistal;
        float t = top-vertal;
        float b = bottom+vertal;
        RectF rect = new RectF(l, t, r, b);
        if (isOval) {
            canvas.drawOval(rect, paint);
        } else {
            canvas.drawRect(rect, paint);
        }

        if (isSelect) {

            Paint roundpaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            roundpaint.setColor(Color.parseColor("#30a6de"));
            roundpaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(left, top, width * 2, roundpaint);
            canvas.drawCircle(left, bottom, width * 2, roundpaint);
            canvas.drawCircle(right, top, width * 2, roundpaint);
            canvas.drawCircle(right, bottom, width * 2, roundpaint);
            Paint linepaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            linepaint.setColor(Color.parseColor("#FFFFFF"));
            linepaint.setStrokeWidth(2);
            linepaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(left, top, width * 2 + 2, linepaint);
            canvas.drawCircle(left, bottom, width * 2 + 2, linepaint);
            canvas.drawCircle(right, top, width * 2 + 2, linepaint);
            canvas.drawCircle(right, bottom, width * 2 + 2, linepaint);
        }
        return this;
    }

    @Override
    public SurfaceShape moveDraw(Canvas canvas, Paint paint) {
        this.scale = 1.0f;
        fixPosition();
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(width);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        RectF rect = new RectF(Math.min(startX, stopX), Math.min(startY, stopY),
                Math.max(startX, stopX), Math.max(startY, stopY));
        if (isOval) {
            canvas.drawOval(rect, paint);
        } else {
            canvas.drawRect(rect, paint);
        }
        return this;
    }

    @Override
    public void fixPosition() {
        left = Math.min(startX, stopX);
        top = Math.min(startY, stopY);
        right = Math.max(startX, stopX);
        bottom = Math.max(startY, stopY);
    }


    @Override
    public RectF getRectF() {
        RectF rectF = new RectF(left, top, right, bottom);
        return rectF;
    }

    @Override
    public void changPosition(RectF rectF, float x, float y, float startX, float startY) {
        this.left = rectF.left + (x - startX);
        this.top = rectF.top + (y - startY);
        this.bottom = rectF.bottom + (y - startY);
        this.right = rectF.right + (x - startX);
    }

    /**
     * @param x
     * @param y
     * @return 0在矩形外 1在矩形内选中移动并删除按钮出现 2 在矩形的边界
     */
    @Override
    public int getState(float x, float y) {
        int margin = width * 2 + 2;
        if (x >= Math.min(left, right) && x < Math.max(left, right) && y >= Math.min(top, bottom) && y < Math.max(top, bottom)) {
            return 1;
        } else {
            CircleShape circleShapeLeftTop = new CircleShape(left, top, margin);
            CircleShape circleShapeRightTop = new CircleShape(right, top, margin);
            CircleShape circleShapeLeftBottom = new CircleShape(left, bottom, margin);
            CircleShape circleShapeRightBottom = new CircleShape(right, bottom, margin);
            if (isOnCircle(x, y, circleShapeLeftTop)) {
                currentPoint = LEFT_TOP;
                return 2;
            }
            if (isOnCircle(x, y, circleShapeRightTop)) {
                currentPoint = RIGHT_TOP;
                return 2;
            }
            if (isOnCircle(x, y, circleShapeLeftBottom)) {
                currentPoint = LEFT_BOTTOM;
                return 2;
            }
            if (isOnCircle(x, y, circleShapeRightBottom)) {
                currentPoint = RIGHT_BOTTOM;
                return 2;
            }
        }
        return 0;
    }


    @Override
    public void changeShape(float x, float y) {
//        super.changeShape(x, y);
        if (currentPoint == LEFT_TOP) {
            left = x;
            top = y;
        } else if (currentPoint == LEFT_BOTTOM) {
            left = x;
            bottom = y;
        } else if (currentPoint == RIGHT_TOP) {
            right = x;
            top = y;
        } else if (currentPoint == RIGHT_BOTTOM) {
            right = x;
            bottom = y;
        }
    }
}
