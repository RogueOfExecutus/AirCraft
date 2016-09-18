package com.wenyi.twodgamedemo.sprite;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2016/9/13.
 */
public class Bullet extends AutoSprite {

    public Bullet(Bitmap bitmap,float x,float y) {
        super(bitmap);
        setSpeed(15);
        setX(x);
        setY(y);
    }

    @Override
    public void move(float x, float y) {
        setY(getY() - getSpeed()*y);
    }
}
