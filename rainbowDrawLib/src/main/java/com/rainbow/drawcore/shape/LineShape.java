package com.rainbow.drawcore.shape;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by rainbow on 16/3/2.
 */
public class LineShape extends SurfaceShape{

    private static final int START = 1;
    private static final int END = 2;
    private static int currentPoint = 0;

    public LineShape(float startX, float startY, float stopX, float stopY) {
        super(startX, startY, stopX, stopY);
    }

    @Override
    public SurfaceShape Draw(Canvas canvas, Paint paint,float scale) {
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(width);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        canvas.drawLine(startX,startY,stopX,stopY,paint);
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
        canvas.drawLine(startX,startY,stopX,stopY,paint);
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
