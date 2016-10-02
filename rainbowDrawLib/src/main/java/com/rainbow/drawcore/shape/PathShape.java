package com.rainbow.drawcore.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by rainbow on 16/3/8.
 */
public class PathShape extends SurfaceShape{
    Path path;


    public PathShape() {
        super(0,0,0,0);
        path = new Path();

    }

    public PathShape(float x, float y) {
        super(0,0,0,0);
        path = new Path();
        path.moveTo(x, y);
        path.lineTo(x, y);
    }

    @Override
    public SurfaceShape Draw(Canvas canvas, Paint paint,float scale) {
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(color);
        paint.setStrokeWidth(width);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawPath(path, paint);
        return this;
    }
    @Override
    public SurfaceShape moveDraw(Canvas canvas, Paint paint) {
        move(stopX,stopY);
        Draw(canvas,paint,1.0f);
        return this;
    }
    public void move(float mx, float my) {
        path.lineTo(mx, my);

    }
}
