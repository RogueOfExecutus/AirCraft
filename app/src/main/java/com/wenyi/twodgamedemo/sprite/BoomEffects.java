package com.wenyi.twodgamedemo.sprite;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by Administrator on 2016/9/14.
 */
public class BoomEffects extends Sprite {
    private int frames = 0;

    public BoomEffects(Bitmap bitmap, float x, float y) {
        super(bitmap);
        setX(x - bitmap.getWidth()/28);
        setY(y - bitmap.getHeight()/2);
    }

    @Override
    public void move(float x, float y) {

    }

    public void next(){
        if(++frames == 28){
            setBroken(true);
        }
    }

    @Override
    public RectF getRectF() {
        float left = getX();
        float top = getY();
        float right = left + getWidth()/14;
        float bottom = top + getHeight();
        return new RectF(left, top, right, bottom);
    }

    @Override
    public Rect getBitmapSrcRec() {
        Rect rect = new Rect();
        rect.left = (int)getWidth()*(frames/2)/14;
        rect.top = 0;
        rect.right = (int)getWidth()*(frames/2+1)/14;
        rect.bottom = (int)getHeight();
        return rect;
    }
}
