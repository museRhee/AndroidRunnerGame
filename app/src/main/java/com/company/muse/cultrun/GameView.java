package com.company.muse.cultrun;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * View where the actual game is executed
 */

public class GameView extends View {
    // Context, Thread
    private Context context;
    private GameThread mThread;

    // screen size
    private int w, h;

    // player, backGround, cult
    static public Player player;
    private BackGround backGround;
    private CultGreen cultGreen;
    private CultRed cultRed;
    private CultBird cultBird;

    // paint setting
    private Paint textPaint;
    private Paint menuPaint;
    private Paint barPaint;

    // gameOver, gameScore
    private boolean isOver;
    private float finalScore;
    static public float midScore;

    //--------------------------
    // constructor
    //--------------------------
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    //--------------------------
    // get the size of View
    //--------------------------
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        this.w = w;     // screen width, height
        this.h = h;

        initGame();

        // start Thread
        if (mThread == null) {
            mThread = new GameThread();
            mThread.start();
        }
    }

    //--------------------------
    // draw image on screen
    //--------------------------
    @Override
    protected void onDraw(Canvas canvas) {
        // background
        canvas.save();    // push canvas
        backGround.draw(canvas);
        canvas.restore(); // pop canvas

        // player
        canvas.save();
        canvas.scale(player.dir.x, 1, player.x, player.y);
        canvas.drawBitmap(player.img, player.x - player.w, player.y - player.h, null);
        canvas.restore();

        // cultGreen
        if (cultGreen.img != null) {  // prevent nullPointException
            canvas.save();
            canvas.drawBitmap(cultGreen.img, cultGreen.x - cultGreen.r, cultGreen.y - cultGreen.r, null);
            canvas.restore();
        }

        // cultRed
        if (cultRed.img != null) {
            canvas.save();
            canvas.drawBitmap(cultRed.img, cultRed.x - cultRed.r, cultRed.y - cultRed.r, null);
            canvas.restore();
        }

        // cultBird
        if (cultBird.img != null) {
            canvas.save();
            canvas.drawBitmap(cultBird.img, cultBird.x - cultBird.r, cultBird.y - cultBird.r, null);
            canvas.restore();
        }

        // gameOver menu
        if (isOver) {
            menuPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("당신의 점수는 " + (int) finalScore + " M", w / 2, h / 3, menuPaint);
            canvas.drawText(" [Touch]  계속하기", w / 2, h / 2, menuPaint);
            canvas.drawText("[BackKey] 돌아가기", w / 2, 2 * h / 3, menuPaint);
        } else {
            //angerBar
            canvas.drawRect(80, 80, 250 * (player.angerCnt), 200, barPaint);

            // angerBar counter
            textPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("분노게이지 : " + player.angerCnt, 100, 160, textPaint);

            // middle score counter
            midScore += (3 * Time.deltaTime);
            textPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText("점수 : " + (int) midScore + " M", w - 100, 160, textPaint);
        }
    }

    //--------------------------
    // move object
    //--------------------------
    private void moveObject() {
        backGround.update();
        player.update();
        cultGreen.update();
        cultRed.update();
        cultBird.update();
    }

    //--------------------------
    // check gameOVer
    //--------------------------
    public void checkOver() {
        if (player.angerCnt <= 10) {
            finalScore = midScore;
            return;
        }
        isOver = true;
    }

    //--------------------------
    // touch event
    //--------------------------
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            player.setAction(event.getX(), event.getY());
        }

        // initialize when gameOver
        if (isOver) {
            initGame();
            isOver = false;
        }
        return true;
    }

    //--------------------------
    // initialize game
    //--------------------------
    private void initGame() {
        backGround = new BackGround(context, w, h);
        player = new Player(context, w, h);
        cultGreen = new CultGreen(context, w, h);
        cultRed = new CultRed(context, w, h);
        cultBird = new CultBird(context, w, h);

        // initScore
        midScore = 0.0f;

        // setting text
        textPaint = new Paint();
        textPaint.setTextSize(60);
        textPaint.setColor(Color.WHITE);
        textPaint.setFakeBoldText(true);

        // setting menu
        menuPaint = new Paint();
        menuPaint.setTextSize(90);
        menuPaint.setColor(Color.WHITE);
        menuPaint.setFakeBoldText(true);

        // setting angerBar
        barPaint = new Paint();
        barPaint.setColor(Color.RED);
    }

    //--------------------------
    // end of View
    //--------------------------
    @Override
    protected void onDetachedFromWindow() {
        mThread.canRun = false;
        super.onDetachedFromWindow();
    }

    //--------------------------
    // Thread Class
    //--------------------------
    class GameThread extends Thread {
        public boolean canRun = true;

        @Override
        public void run() {
            while (canRun) {
                try {
                    Time.update();      // get delTime
                    moveObject();
                    checkOver();
                    postInvalidate();   // View invalidation(-->then onDraw called)

                    sleep(10);
                } catch (Exception e) {

                }
            }
        }
    }
}
