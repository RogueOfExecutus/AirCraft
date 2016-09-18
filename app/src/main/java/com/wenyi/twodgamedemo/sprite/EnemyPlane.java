package com.wenyi.twodgamedemo.sprite;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2016/9/13.
 */
public abstract class EnemyPlane extends AutoSprite {
    private int health, award;
    private float width, height;

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getAward() {
        return award;
    }

    public void setAward(int award) {
        this.award = award;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void heat(){
        if(--health == 0){
            setBroken(true);
        }
    }

    public EnemyPlane(Bitmap bitmap,int speed,float x) {
        super(bitmap);
        setSpeed(speed);
        setX(x);
        setY(0);
        setWidth(bitmap.getWidth());
        setHeight(bitmap.getHeight());
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public void move(float x, float y) {
        setY(getY() + getSpeed()*y);
    }
}
