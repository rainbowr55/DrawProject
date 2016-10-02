package com.rainbow.drawcore.shape;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

/**
 * Created by rainbow on 16/3/2.
 */
public class ALShape extends SurfaceShape{

    private static final int START = 1;
    private static final int END = 2;
    private static int currentPoint = 0;

    public ALShape(float startX, float startY, float stopX, float stopY) {
        super(startX, startY, stopX, stopY);
    }


    /**
     * 画箭头
     * @param sx
     * @param sy
     * @param ex
     * @param ey
     */
    public void drawAL(Canvas canvas,Paint paint,float sx, float sy, float ex, float ey)
    {
        double H = 22; // 箭头高度
        double L = 16; // 底边的一半
        int x3 = 0;
        int y3 = 0;
        int x4 = 0;
        int y4 = 0;
        double awrad = Math.atan(L / H); // 箭头角度
        double arraow_len = Math.sqrt(L * L + H * H); // 箭头的长度
        double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
        double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
        double x_3 = ex - arrXY_1[0]; // (x3,y3)是第一端点
        double y_3 = ey - arrXY_1[1];
        double x_4 = ex - arrXY_2[0]; // (x4,y4)是第二端点
        double y_4 = ey - arrXY_2[1];
        Double X3 = new Double(x_3);
        x3 = X3.intValue();
        Double Y3 = new Double(y_3);
        y3 = Y3.intValue();
        Double X4 = new Double(x_4);
        x4 = X4.intValue();
        Double Y4 = new Double(y_4);
        y4 = Y4.intValue();
        // 画线
        canvas.drawLine(sx, sy, ex, ey,paint);
        Path triangle = new Path();
        triangle.moveTo(ex, ey);
        triangle.lineTo(x3, y3);
        triangle.lineTo(x4, y4);
        triangle.close();
        canvas.drawPath(triangle,paint);

    }

    // 计算
    public double[] rotateVec(float px, float py, double ang, boolean isChLen, double newLen)
    {
        double mathstr[] = new double[2];
        // 矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度、新长度
        double vx = px * Math.cos(ang) - py * Math.sin(ang);
        double vy = px * Math.sin(ang) + py * Math.cos(ang);
        if (isChLen) {
            double d = Math.sqrt(vx * vx + vy * vy);
            vx = vx / d * newLen;
            vy = vy / d * newLen;
            mathstr[0] = vx;
            mathstr[1] = vy;
        }
        return mathstr;
    }

    @Override
    public SurfaceShape Draw(Canvas canvas, Paint paint,float scale) {
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(width);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
//        canvas.d(startX, startY, stopX, stopY, paint);
        drawAL(canvas,paint,startX,startY,stopX,stopY);
        if (isSelect) {

            Paint roundpaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            roundpaint.setColor(Color.parseColor("#30a6de"));
            roundpaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(startX, startY, width * 2, roundpaint);
            canvas.drawCircle(stopX, stopY, width * 2, roundpaint);
            Paint linepaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            linepaint.setColor(Color.parseColor("#FFFFFF"));
            linepaint.setStrokeWidth(2);
            linepaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(startX, startY, width * 2 + 2, linepaint);
            canvas.drawCircle(stopX, stopY, width * 2 + 2, linepaint);

        }
        return this;
    }

    @Override
    public SurfaceShape moveDraw(Canvas canvas, Paint paint) {
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(width);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        drawAL(canvas,paint,startX,startY,stopX,stopY);
        return this;
    }

    @Override
    public int getState(float x, float y) {
        float rx = x;
        float ry= (Math.abs((x-startX)))*(Math.abs((stopY - startY)))/(Math.abs((stopX - startX)))+startY;
        float ry1 = startY -(Math.abs((x-startX)))*(Math.abs((stopY - startY)))/(Math.abs((stopX - startX)));
        CircleShape circleShape = new CircleShape(rx, ry, margin);
        CircleShape circleShape1 = new CircleShape(rx,ry1,margin);
        CircleShape circleShapeStart = new CircleShape(startX, startY, margin);
        CircleShape circleShapeEnd = new CircleShape(stopX, stopY, margin);
        if (isOnCircle(x, y, circleShapeStart)) {
            currentPoint = START;
            return 2;
        }
        if (isOnCircle(x, y, circleShapeEnd)) {
            currentPoint = END;
            return 2;
        }
        if(x>Math.min(startX,stopX)&&x<Math.max(startX,stopX)&&y>Math.min(startY,stopY)&&y<Math.max(startY,stopY)){

            if(isOnCircle(x,y,circleShape)||isOnCircle(x,y,circleShape1)){
                return 1;
            }

        }
        return 0;
    }

    @Override
    public RectF getRectF() {
        RectF rectF = new RectF(startX,startY,stopX,stopY);
        return rectF;
    }

    @Override
    public void changPosition(RectF rectF, float x, float y, float startX, float startY) {
        this.startX = rectF.left + (x - startX);
        this.startY = rectF.top + (y - startY);
        this.stopX =rectF.right + (x - startX) ;
        this.stopY = rectF.bottom + (y - startY);
    }

    @Override
    public void changeShape(float x, float y) {
        if(currentPoint==START){
            this.startX = x;
            this.startY = y;
        }else if(currentPoint==END){
            this.stopX = x;
            this.stopY = y;
        }
    }
}
