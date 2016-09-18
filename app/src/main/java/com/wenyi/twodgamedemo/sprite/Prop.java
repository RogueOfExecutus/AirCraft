package com.wenyi.twodgamedemo.sprite;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2016/9/14.
 */
public abstract class Prop extends AutoSprite {
    public Prop(Bitmap bitmap,float x,float y) {
        super(bitmap);
        setSpeed(2);
        setX(x - bitmap.getWidth()/2);
        setY(y - bitmap.getHeight()/2);
    }

    @Override
    public void move(float x, float y) {
        setY(getY() + getSpeed()*y);
    }
}
