package com.wenyi.twodgamedemo.sprite;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2016/9/14.
 */
public class BigEnemyPlane extends EnemyPlane {
    public BigEnemyPlane(Bitmap bitmap, int speed, float x) {
        super(bitmap, speed, x);
        setHealth(10);
        setAward(4000);
    }
}
