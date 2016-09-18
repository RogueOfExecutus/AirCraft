package com.wenyi.twodgamedemo.sprite;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by Administrator on 2016/9/9.
 * 精灵类
 */
public abstract class Sprite {
    private boolean broken = false;
    private float x = 0;
    private float y = 0;
    private Bitmap bitmap = null;
    public boolean isBroken() {
        return broken;
    }
    public void setBroken(boolean broken) {
        this.broken = broken;
        if(broken){
            bitmap = null;
        }
    }
    public float getX() {
        return x;
    }
    public void setX(float x) {
        this.x = x;
    }
    public float getY() {
        return y;
    }
    public void setY(float y) {
        this.y = y;
    }
    public Bitmap getBitmap() {
        return bitmap;
    }
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Sprite(Bitmap bitmap){
        this.bitmap = bitmap;
    }

    public float getWidth(){
        if(bitmap != null){
            return bitmap.getWidth();
        }
        return 0;
    }

    public float getHeight(){
        if(bitmap != null){
            return bitmap.getHeight();
        }
        return 0;
    }

    abstract public void move(float x, float y);

    public RectF getRectF(){
        float left = x;
        float top = y;
        float right = left + getWidth();
        float bottom = top + getHeight();
        return new RectF(left, top, right, bottom);
    }
    public Rect getBitmapSrcRec(){
        Rect rect = new Rect();
        rect.left = 0;
        rect.top = 0;
        rect.right = (int)getWidth();
        rect.bottom = (int)getHeight();
        return rect;
    }
    public Point getCollidePointWithOther(Sprite s){
        Point p = null;
        RectF rectF1 = getRectF();
        RectF rectF2 = s.getRectF();
        RectF rectF = new RectF();
        boolean isIntersect = rectF.setIntersect(rectF1, rectF2);
        if(isIntersect){
            p = new Point(Math.round(rectF.centerX()), Math.round(rectF.centerY()));
        }
        return p;
    }
    public void onDraw(Canvas canvas){
        if(bitmap != null && !broken){
            //将Sprite绘制到Canvas上
            Rect srcRef = getBitmapSrcRec();
            RectF dstRecF = getRectF();
            //canvas.drawBitmap(this.bitmap, x, y, paint);
            canvas.drawBitmap(bitmap, srcRef, dstRecF, null);
        }
    }
}
