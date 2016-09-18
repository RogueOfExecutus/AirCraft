package com.wenyi.twodgamedemo.sprite;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2016/9/9.
 */
public class Aircraft extends Sprite {

    public Aircraft(Bitmap bitmap) {
        super(bitmap);
    }

    @Override
    public void move(float x, float y) {

        setX(x - getWidth()/2);
        setY(y - getHeight()/2);
    }

    @Override
    public float getX() {
        return (super.getX() + this.getWidth()/2);
    }

    @Override
    public float getY() {
        return (super.getY() + this.getHeight()/2);
    }
}
