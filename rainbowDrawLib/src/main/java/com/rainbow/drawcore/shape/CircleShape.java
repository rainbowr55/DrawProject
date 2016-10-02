package com.rainbow.drawcore.shape;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

/**
 * Created by rainbow on 16/3/1.
 */
public class CircleShape extends SurfaceShape {

    public float radius;//半径

    //圆心坐标
    public float x;
    public float y;


    public CircleShape(float startX, float startY, float stopX, float stopY) {
        super(startX, startY, stopX, stopY);

    }

    public CircleShape(float x, float y, float radius){
        super(0,0,0,0);
        this.x = x;
        this.y= y;
        this.radius = radius;
    }

    /**
     * @param x
     * @param y
     * @return 0在圆形外 1在圆形内选中移动并删除按钮出现 2 在圆形的边界
     */
    @Override
    public int getState(float x, float y) {


        float distance = (float) Math.sqrt(Math.pow(
                Math.abs(x - (this.x)), 2) + Math.pow(Math.abs(y - (this.y)), 2));//到圆心的距离
        if (distance < 0) {
            return 0;
        }
        if (distance > radius+10) {
            return 0;
        }
        if (distance < radius - 10) {
            return 1;
        }
        if (distance < radius+10 && distance >= radius - 10) {
            return 2;

        }
        Log.e("test", "distance:" + distance + " , radius:" + radius);

        return 0;
    }

    @Override
    public CircleShape Draw(Canvas canvas, Paint paint,float scale) {
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(width);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(x, y, radius, paint);
        if(isSelect){
            Paint linepaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            linepaint.setColor(Color.BLUE);
            linepaint.setStrokeWidth(5);
            linepaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(x, y, radius + width, linepaint);
            canvas.drawCircle(x, y, radius - width, linepaint);
        }
        return this;
    }

    @Override
    public SurfaceShape moveDraw(Canvas canvas, Paint paint) {
        fixPosition();
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(width);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle((startX + stopX) / 2, (startY + stopY) / 2, radius, paint);
        return this;
    }

    @Override
    public void fixPosition() {
        x = (startX + stopX) / 2;
        y = (startY + stopY) / 2;
        radius = (float) Math.sqrt(Math.pow(
                startX - stopX, 2) + Math.pow(startY - stopY, 2)) / 2;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    @Override
    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    public float getRadius() {
        return radius;
    }

    @Override
    public void changPosition(RectF rectF, float x, float y, float startX, float startY) {
        this.setX(x);
        this.setY(y);
    }
}
