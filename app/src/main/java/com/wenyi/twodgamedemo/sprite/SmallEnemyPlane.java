package com.wenyi.twodgamedemo.sprite;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2016/9/13.
 */
public class SmallEnemyPlane extends EnemyPlane {

    public SmallEnemyPlane(Bitmap bitmap, int speed, float x) {
        super(bitmap, speed, x);
        setHealth(1);
        setAward(200);
    }
}
