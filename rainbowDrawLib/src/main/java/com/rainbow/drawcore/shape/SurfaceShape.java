package com.rainbow.drawcore.shape;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by rainbow on 16/3/1.
 */
public class SurfaceShape {
    public static final int LINE_WIDTH = 10;
    public float startX;
    public float startY;
    public float stopX;
    public float stopY;

    public boolean isSelect;
    public int color = Color.parseColor("#ef3030");
    public int margin = 20;
    public int width = 10;
    public float scale = 1.0f;
    public int getState(float x,float y){
        return 0;
    }

    public SurfaceShape(float startX, float startY, float stopX, float stopY) {
        this.startX = startX;
        this.startY = startY;
        this.stopX = stopX;
        this.stopY = stopY;
    }

    public SurfaceShape Draw(Canvas canvas,Paint paint,float scale){

        return this;
    }


    public SurfaceShape moveDraw(Canvas canvas,Paint paint){


        return this;
    }
    public void fixPosition(){

    }

    public float getX(){
        return 0;
    }
    public float getY(){
        return 0;
    }

    public void setX(float x){

    }

    public void setY(float y){

    }

    public void setRadius(float radius){

    }
    public float getRadius(){
        return 0;
    }

    public void setSelect(boolean isSelect){
        this.isSelect = isSelect;
    }

    public boolean isSelect(){
        return this.isSelect;
    }

    public void changeShape(float x,float y){}

    public void changPosition(RectF rectF,float x,float y,float startX,float startY){

    }

    public RectF getRectF(){
        return null;
    }

    public void changeColor(int color){
        this.color = color;
    }

    public boolean isOnRectangle(float x, float y, RectF rectF) {
        if (x >= rectF.left && x <= rectF.right && y >= rectF.top && y <= rectF.bottom) {//在矩形内
            return true;
        }
        return false;
    }

    public boolean isOnCircle(float x, float y, CircleShape circleShape) {
        float distance = (float) Math.sqrt(Math.pow(
                Math.abs(x - (circleShape.x)), 2) + Math.pow(Math.abs(y - (circleShape.y)), 2));//到圆心的距离
        if (distance < 0) {
            return false;
        }
        if (distance >= circleShape.radius + margin) {
            return false;
        }
        if (distance < circleShape.radius + margin) {
            return true;
        }

        return false;
    }

}
