package com.wenyi.twodgamedemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Administrator on 2016/9/6.
 */
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    boolean running;
    SurfaceHolder holder;
    float oneX,oneY,twoX,twoY;
    public MySurfaceView(Context context) {
        super(context);
        oneX = 50.0f;
        oneY = 50.0f;
        twoX = 200.0f;
        twoY = 50.0f;
        running = true;
        holder = this.getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running){
                    try {
                        drowFour();
                        Thread.sleep(500);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (running){
//                    try {
//                        drowTwo();
////                        Thread.sleep(1);
//                    } catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
    }

    private void drowFour() {
        Canvas canvas = null;
        try{
            canvas = holder.lockCanvas();
            Rect rect = new Rect(0,0,50,50);
            RectF rectf = new RectF(120.0f,120.0f,300.0f,300.0f);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            canvas.drawBitmap(bitmap,rect,rectf,new Paint());
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            holder.unlockCanvasAndPost(canvas);
        }
    }

    float ro = 0.1f;
    private void drowThree() {
        Canvas canvas = null;
        try {
            canvas = holder.lockCanvas();
            Paint mPaint = new Paint();
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawPaint(mPaint);
            mPaint.setXfermode(null);
            mPaint.setColor(Color.BLUE);
            canvas.drawRect(100, 200, 200, 300, mPaint);
            canvas.save();  //注释1
            canvas.rotate(ro,175,35);
            mPaint.setColor(Color.RED);
            canvas.drawRect(150, 10, 200, 60, mPaint);
            canvas.restore(); //注释2
            holder.unlockCanvasAndPost(canvas);
            canvas = holder.lockCanvas(new Rect(150,10,200,60));
            mPaint.setColor(Color.GREEN);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            canvas.drawBitmap(bitmap,100.0f,10.0f,mPaint);
//            canvas.drawRect(180, 10, 250, 100, mPaint);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            ro += 0.1f;
            holder.unlockCanvasAndPost(canvas);
        }
    }

    boolean isDrawOne = true;
    private void drowTwo() {
        synchronized (holder) {
            Canvas canvas = null;
            try {
                if(isDrawOne)
                    holder.wait();
                canvas = holder.lockCanvas(new Rect((int) twoX, (int) oneY, (int) (twoX + 1.0f), (int) (oneY + 1.0f)));
                Paint p = new Paint();
                p.setColor(Color.WHITE);
                p.setStrokeWidth(20);
                canvas.drawLine(twoX, oneY, twoX + 1.0f, oneY + 1.0f, p);
                twoX += 1.0f;
                oneY += 1.0f;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
                isDrawOne = true;
                holder.notify();
            }
        }
    }

    private void drowOne(){
        synchronized (holder) {
            Canvas canvas = null;
            try {
                if(!isDrawOne)
                    holder.wait();
                canvas = holder.lockCanvas(new Rect((int) oneX, (int) oneY, (int) (oneX + 1.0f), (int) (oneY + 1.0f)));
                Paint p = new Paint();
                p.setColor(Color.RED);
                p.setStrokeWidth(20);
                canvas.drawLine(oneX, oneY, oneX + 1.0f, oneY + 1.0f, p);
                oneX += 1.0f;
                oneY += 1.0f;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
                isDrawOne = false;
                holder.notify();
            }
        }
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        running = false;
    }
}
