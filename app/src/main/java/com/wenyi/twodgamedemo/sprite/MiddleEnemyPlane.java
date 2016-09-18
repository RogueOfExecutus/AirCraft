package com.wenyi.twodgamedemo.sprite;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2016/9/14.
 */
public class MiddleEnemyPlane extends EnemyPlane {
    public MiddleEnemyPlane(Bitmap bitmap, int speed, float x) {
        super(bitmap, speed, x);
        setHealth(4);
        setAward(1000);
    }
}
