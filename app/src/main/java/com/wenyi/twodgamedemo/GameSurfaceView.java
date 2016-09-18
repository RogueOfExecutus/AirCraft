package com.wenyi.twodgamedemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.wenyi.twodgamedemo.sprite.Aircraft;
import com.wenyi.twodgamedemo.sprite.BigEnemyPlane;
import com.wenyi.twodgamedemo.sprite.Bomb;
import com.wenyi.twodgamedemo.sprite.BoomEffects;
import com.wenyi.twodgamedemo.sprite.Bullet;
import com.wenyi.twodgamedemo.sprite.DoubleGun;
import com.wenyi.twodgamedemo.sprite.EnemyPlane;
import com.wenyi.twodgamedemo.sprite.MiddleEnemyPlane;
import com.wenyi.twodgamedemo.sprite.Prop;
import com.wenyi.twodgamedemo.sprite.SmallEnemyPlane;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2016/9/9.
 * 游戏界面
 */
public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static final int[] bitmapIds = {R.mipmap.plane,R.mipmap.explosion,R.mipmap.yellow_bullet, R.mipmap.blue_bullet,
            R.mipmap.small, R.mipmap.middle, R.mipmap.big, R.mipmap.bomb_award,R.mipmap.bullet_award,R.mipmap.pause1,
            R.mipmap.pause2, R.mipmap.bomb, R.mipmap.bg};
    private Thread thread;
    private static Bitmap[] bitmaps = new Bitmap[13];
    private Bitmap pauseBitmap;
    private boolean running, touchHold, pause, gameOver;
    private SurfaceHolder holder;
    private int width,height,minute,second,bulletInterval,score,enemyRate,bulletRate,doubleBullets,bombs;
    private Aircraft aircraft;
    private float touchDownX,touchDownY,touchUpX,touchUpY;
    private Rect pauseRect;
    private final Object lock = new Object();
    private long touchDownTime,touchUpTime,lastDownTime,lastUpTime,gameTime,gameStartTime,gamePauseTime;
    private final List<Bullet> allBullet = new ArrayList<>();
    private final List<EnemyPlane> allPlane = new ArrayList<>();
    private final List<BoomEffects> allBoom = new ArrayList<>();
    private final List<Prop> allProp = new ArrayList<>();

    public GameSurfaceView(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        //开始时是否处于暂停状态
        pause = false;
        gameOver = false;
        enemyRate = 700;
        bulletRate = 150;
        for(int i=0;i<13;i++){
            bitmaps[i] = BitmapFactory.decodeResource(getResources(),bitmapIds[i]);
        }
        pauseBitmap = bitmaps[9];
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        running = true;
        width = getWidth();
        height = getHeight();
        System.out.println(width+"-----"+height);
        if(thread == null) {
            pauseRect = new Rect(width/40,height/70,width/10,height/14);
            aircraft = new Aircraft(bitmaps[0]);
            aircraft.move(width/2,height - bitmaps[0].getHeight()/2);
            thread = new Thread(this);
            thread.start();
            countTime();
        }else {
            reFlash();
            if(pause && !gameOver) {
                drawPause("当前分数", "继续游戏");
            }
        }
    }

    @Override
    public void run() {
        gameStartTime = System.currentTimeMillis();
        while (running){
            synchronized (lock) {
                if (!pause) {
                    checkHeatSelf();
                    checkHeatEnemy();
                    checkGetProp();
                    checkBullet();
                    checkPlane();
                    checkProp();
                    holdMove();
                    reFlash();
                }else {
                    try {
                        //暂停时间，绘制暂停效果
                        if(!gameOver) {
                            drawPause("当前分数", "继续游戏");
                        }
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void onPause(){
        pause = true;
    }
    public void onResume(){
        synchronized (lock){
            pause = false;
            lock.notifyAll();
        }
    }
    public void onDestroy(){
        synchronized (lock){
            running = false;
            lock.notifyAll();
        }
    }
    //绘制暂停
    public void drawPause(String title,String bt){
        Canvas canvas = null;
        try{
            float x = width, y = height;
            //只获取dialog大小，避免双缓存引发的黑屏、闪屏
            canvas = holder.lockCanvas(new Rect((int)x/5,(int)y/3,(int)x*4/5,(int)y*2/3));
            Paint p = new Paint(), tp = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
            p.setStyle(Paint.Style.FILL);
            p.setColor(0xFFD7DDDE);
            RectF dialog = new RectF(x/5,y/3,x*4/5,y*2/3);
            canvas.drawRect(dialog,p);
            p.setStyle(Paint.Style.STROKE);
            p.setColor(0xFF515151);
            p.setStrokeWidth(2);
            canvas.drawLine(x/5,y/3,x*4/5,y/3,p);
            canvas.drawLine(x/5,y/3,x/5,y*2/3,p);
            canvas.drawLine(x/5,y*2/3,x*4/5,y*2/3,p);
            canvas.drawLine(x*4/5,y/3,x*4/5,y*2/3,p);
            canvas.drawLine(x/5,y*5/12,x*4/5,y*5/12,p);
            canvas.drawLine(x/5,y*7/12,x*4/5,y*7/12,p);
            tp.setTextSize(40.0f*(float)width/720.0f);
            tp.setColor(0xff000000);
            tp.setStrokeWidth(3);
            tp.setTextAlign(Paint.Align.CENTER);
            Paint.FontMetrics fm = tp.getFontMetrics();
            canvas.drawText(title,x/2,y*9/24 - fm.ascent/2,tp);
            //draw 分数
            canvas.drawText(bt,x/2,y*5/8 - fm.ascent/2,tp);
            tp.setTextSize(70);
            canvas.drawText(score+"",x/2,y/2 - fm.ascent/2,tp);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(canvas != null)
                holder.unlockCanvasAndPost(canvas);
        }
    }
    public void reFlash(){
        Canvas canvas = null;
        try{
            canvas = holder.lockCanvas();
            Paint p = new Paint(), tp = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
            p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawPaint(p);
            p.setXfermode(null);
            //背景图
            canvas.drawBitmap(bitmaps[12],new Rect(0,0,bitmaps[12].getHeight()*width/height,bitmaps[12].getHeight()),
                    new Rect(0,0,width,height),p);
            drawBoom(canvas);
            drawBullet(canvas);
            drawPlane(canvas);
            drawProp(canvas);
            drawBombs(canvas);
            //暂停按钮
            canvas.drawBitmap(pauseBitmap,new Rect(0,0,pauseBitmap.getWidth(),pauseBitmap.getHeight()), pauseRect,p);
            tp.setTextSize(40.0f*(float)width/720.0f);
            tp.setColor(0xff000000);
            tp.setStrokeWidth(3);
            canvas.drawText(score+"",width/7,height/20,tp);
            //时间
//            canvas.drawText(minute+":"+second,width*6/7,height/12,p);
            aircraft.onDraw(canvas);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(canvas != null)
                holder.unlockCanvasAndPost(canvas);
        }
    }

    public void holdMove(){
        if(touchHold){
            checkMove(touchDownX,touchDownY);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE){
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                lastDownTime = touchDownTime;
                touchDownTime = System.currentTimeMillis();
            }
            touchDownX = event.getX();
            touchDownY = event.getY();
            if(touchDownX < width/8 && touchDownY < height/10){
                pauseBitmap = bitmaps[10];
            } else {
                pauseBitmap = bitmaps[9];
                touchHold = true;
            }
            return true;
        }else if(event.getAction() == MotionEvent.ACTION_UP){
            lastUpTime = touchUpTime;
            touchUpTime = System.currentTimeMillis();
            if(!pause) {
                int status = checkClick();
                if (status == 2) {
                    //双击
                    useBomb();
                }
            }
            touchUpX = event.getX();
            touchUpY = event.getY();
            pauseBitmap = bitmaps[9];
            if(!pause && touchUpX < width/8 && touchUpY < height/10){
                pause = true;
            }
            if(pause && touchUpX >= width/5 && touchUpY >= height*7/12 && touchUpX <= width*4/5 && touchUpY <= height*2/3){
                //继续游戏
                if(gameOver){
                    allPlane.clear();
                    allBullet.clear();
                    allBoom.clear();
                    score = 0;
                    doubleBullets = 0;
                    bombs = 0;
                    aircraft = new Aircraft(bitmaps[0]);
                    aircraft.move(width/2,height - bitmaps[0].getHeight()/2);
                    gameOver = false;
                }
                onResume();
            }
            touchHold = false;
        }
        return super.onTouchEvent(event);
    }
    public void countTime(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running){
                    try {
                        if(pause){
                            gamePauseTime = System.currentTimeMillis() - gameStartTime - gameTime;
                        }else {
                            gameTime = System.currentTimeMillis() - gameStartTime - gamePauseTime;
                            if((int) gameTime / 60000 > minute) {
                                minute = (int) gameTime / 60000;
                                if(enemyRate > 300) {
                                    enemyRate -= 30;
                                }
                                if(bulletRate > 80) {
                                    bulletRate -= 5;
                                }
                            }
                            //敌机刷新间隔时间
                            if((int) gameTime / enemyRate != second) {
                                createPlane();
                                second = (int) gameTime / enemyRate;
                            }
                            //子弹发射间隔时间
                            if((int) gameTime / bulletRate != bulletInterval) {
                                createBullet();
                                bulletInterval = (int) gameTime / bulletRate;
                            }
                        }
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    public void createPlane(){
        Random r = new Random();
        int num = r.nextInt(10);
        EnemyPlane plane;
        if(num < 7) {
            plane = new SmallEnemyPlane(bitmaps[4], r.nextInt(5) + 1, r.nextInt(width - bitmaps[4].getWidth()));
        }else if(num < 9){
            plane = new MiddleEnemyPlane(bitmaps[5],r.nextInt(3) + 1, r.nextInt(width - bitmaps[5].getWidth()));
        }else {
            plane = new BigEnemyPlane(bitmaps[6],r.nextInt(2) + 1, r.nextInt(width - bitmaps[6].getWidth()));
        }
        synchronized (allPlane){
            allPlane.add(plane);
        }
    }
    public void drawPlane(Canvas canvas){
        synchronized (allPlane) {
            for (EnemyPlane plane : allPlane) {
                plane.onDraw(canvas);
            }
        }
    }
    public void checkPlane(){
        synchronized (allPlane){
            Iterator<EnemyPlane> it = allPlane.iterator();
            while (it.hasNext()){
                EnemyPlane plane = it.next();
                if(plane.getY() > height){
                    it.remove();
                }else if(plane.isBroken()){
                    score += plane.getAward();
                    allBoom.add(new BoomEffects(bitmaps[1],plane.getX()+plane.getWidth()/2,plane.getY()+plane.getHeight()/2));
                    int gift = new Random().nextInt(200);
                    if(gift > 196){
                        allProp.add(new DoubleGun(bitmaps[8],plane.getX()+plane.getWidth()/2,plane.getY()+plane.getHeight()/2));
                    }else if(gift > 193){
                        allProp.add(new Bomb(bitmaps[7],plane.getX()+plane.getWidth()/2,plane.getY()+plane.getHeight()/2));
                    }
                    it.remove();
                }else {
                    plane.move(0,height/500);
                }
            }
        }
    }
    public void drawProp(Canvas canvas){
        synchronized (allProp){
            for (Prop prop:allProp) {
                prop.onDraw(canvas);
            }
        }
    }
    public void checkProp(){
        synchronized (allProp) {
            Iterator<Prop> it = allProp.iterator();
            while (it.hasNext()) {
                Prop prop = it.next();
                if(prop.getY() > height || prop.isBroken()){
                    it.remove();
                } else {
                    prop.move(0,height/500);
                }
            }
        }
    }
    public void checkGetProp(){
        synchronized (allProp){
            for (Prop prop:allProp) {
                if(prop.getCollidePointWithOther(aircraft) != null){
                    prop.setBroken(true);
                    if(prop instanceof DoubleGun){
                        doubleBullets += 150;
                    }
                    if(prop instanceof Bomb){
                        bombs++;
                    }
                }
            }
        }
    }
    public void useBomb(){
        if(bombs > 0) {
            synchronized (allPlane) {
                for (EnemyPlane plane : allPlane) {
                    plane.setBroken(true);
                }
            }
            bombs--;
        }
    }
    //炸弹数
    public void drawBombs(Canvas canvas){
        if(bombs > 0){
            canvas.drawBitmap(bitmaps[11],width/40,height*69/70 - bitmaps[11].getHeight(),null);
            Paint tp = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
            tp.setTextSize(40.0f*(float)width/720.0f);
            tp.setColor(0xff000000);
            tp.setStrokeWidth(3);
            tp.setTextAlign(Paint.Align.LEFT);
            Paint.FontMetrics fm = tp.getFontMetrics();
            canvas.drawText("X"+bombs,width/39 + bitmaps[11].getWidth(),height*69/70 - bitmaps[11].getHeight()/2 - fm.ascent/2,tp);
        }
    }
    //爆炸效果
    public void drawBoom(Canvas canvas){
        Iterator<BoomEffects> it = allBoom.iterator();
        while (it.hasNext()){
            BoomEffects boom = it.next();
            if(boom.isBroken()){
                it.remove();
            }else {
                boom.onDraw(canvas);
                boom.next();
            }
        }
    }
    public void createBullet() {
        if(gameOver){
            return;
        }
        if(doubleBullets > 0){
            Bullet left = new Bullet(bitmaps[2], aircraft.getX() - bitmaps[2].getWidth() / 2 - bitmaps[0].getWidth()/3,
                    aircraft.getY() - bitmaps[2].getHeight() - bitmaps[0].getHeight() / 2 + bitmaps[0].getHeight()/3);
            Bullet right = new Bullet(bitmaps[2], aircraft.getX() - bitmaps[2].getWidth() / 2 + bitmaps[0].getWidth()/3,
                    aircraft.getY() - bitmaps[2].getHeight() - bitmaps[0].getHeight() / 2 + bitmaps[0].getHeight()/3);
            synchronized (allBullet){
                allBullet.add(left);
                allBullet.add(right);
                doubleBullets --;
            }
        }else {
            Bullet bullet = new Bullet(bitmaps[2], aircraft.getX() - bitmaps[2].getWidth() / 2,
                    aircraft.getY() - bitmaps[2].getHeight() - bitmaps[0].getHeight() / 2);
            synchronized (allBullet) {
                allBullet.add(bullet);
            }
        }
    }

    public void drawBullet(Canvas canvas) {
        synchronized (allBullet) {
            for (Bullet bullets : allBullet) {
                bullets.onDraw(canvas);
            }
        }
    }
    //移动以及消除子弹
    public void checkBullet(){
//        for (Bullet bullets:allBullet) {
//            bullets.move(0,height/1000);
//        }
        synchronized (allBullet) {
            Iterator<Bullet> iterator = allBullet.iterator();
            while (iterator.hasNext()) {
                Bullet b = iterator.next();
                if (b.getY() < 0 || b.isBroken()) {
                    iterator.remove();
                } else {
                    b.move(0, height / 500);
                }
            }
        }
    }
    public void checkHeatEnemy(){
        synchronized (allPlane) {
            for (EnemyPlane plane : allPlane) {
                synchronized (allBullet) {
                    for (Bullet b : allBullet) {
                        if (plane.getCollidePointWithOther(b) != null) {
                            plane.heat();
                            b.setBroken(true);
                            if(plane.isBroken()){
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    public boolean checkHeatSelf(){
        synchronized (allPlane){
            for (EnemyPlane plane : allPlane){
                if(!plane.isBroken() && plane.getCollidePointWithOther(aircraft) != null){
//                    drawPause("GAME OVER","重新开始");
                    gameOver = true;
                    allBoom.add(new BoomEffects(bitmaps[1],aircraft.getX(),aircraft.getY()));
                    aircraft.setBroken(true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            pause = true;
                            drawPause("GAME OVER","重新开始");
                        }
                    }).start();
                    return true;
                }
            }
        }
        return false;
    }
    public int checkClick(){
        if(touchUpTime - touchDownTime < 200 && lastUpTime - lastDownTime < 200 && touchUpTime - lastUpTime < 300){
            return 2;
        }
        return 0;
    }
    public void checkMove(float x,float y){
        float airX = aircraft.getX(),airY = aircraft.getY();
        if (Math.sqrt(Math.pow((airX - x), 2) + Math.pow((airY - y), 2)) >= 30.0f){
            double tempRad = getRad(airX, airY, x, y);
            getXY(airX, airY,30.0f,tempRad);
        }else {
            moveAircraft(x,y);
        }
    }
    /***
     * 得到两点之间的弧度
     */
    public double getRad(float px1, float py1, float px2, float py2) {
        //得到两点X的距离
        float x = px2 - px1;
        //得到两点Y的距离
        float y = py1 - py2;
        //算出斜边长
        float xie = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        //得到这个角度的余弦值（通过三角函数中的定理 ：邻边/斜边=角度余弦值）
        float cosAngle = x / xie;
        //通过反余弦定理获取到其角度的弧度
        float rad = (float) Math.acos(cosAngle);
        //注意：当触屏的位置Y坐标<摇杆的Y坐标我们要取反值-0~-180
        if (py2 < py1) {
            rad = -rad;
        }
        return rad;
    }
    public void getXY(float centerX, float centerY, float R, double rad) {
        //获取圆周运动的X坐标
//        aircraft.move((float) (R * Math.cos(rad)) + centerX,(float) (R * Math.sin(rad)) + centerY);
        moveAircraft((float) (R * Math.cos(rad)) + centerX,(float) (R * Math.sin(rad)) + centerY);
        //获取圆周运动的Y坐标
    }
    public void moveAircraft(float x,float y){
        float maxX = width - bitmaps[0].getWidth()/2, maxY = height - bitmaps[0].getHeight()/2;
        float rx = x <= maxX ? (x > bitmaps[0].getWidth()/2 ? x : bitmaps[0].getWidth()/2) : maxX ,
                ry = y <= maxY ? (y > bitmaps[0].getHeight()/2 ? y : bitmaps[0].getHeight()/2) : maxY;
        aircraft.move(rx, ry);
    }
}
