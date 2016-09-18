package com.wenyi.twodgamedemo.sprite;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2016/9/13.
 */
public abstract class AutoSprite extends Sprite {
    private int speed;

    public AutoSprite(Bitmap bitmap) {
        super(bitmap);
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

}
