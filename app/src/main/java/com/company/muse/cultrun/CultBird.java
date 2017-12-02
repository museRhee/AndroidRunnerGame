package com.company.muse.cultrun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;

import java.util.Random;

/**
 * Setting Obstacle_CultBird's images, position, direction, animation...
 */

public class CultBird {
    // screen, CultBird size
    private int scrW, scrH;
    public int w, h;

    // current position, radius, speed
    public float x, y;
    public int r;
    private float speed;

    // move direction, animation, bitmap
    private PointF dir = new PointF();
    private int aniNum = 0;
    private float aniSpan = 0.10f;
    private float aniTime;
    private Bitmap[][] arImg = new Bitmap[5][4];
    public Bitmap img;  // <-- GameView.onDraw


    //--------------------------
    // Constructor
    //--------------------------
    public CultBird(Context context, int width, int height) {
        scrW = width;
        scrH = height;

        // bitmap, initialize
        makeBitmap(context);
        init();
    }

    //--------------------------
    // Move
    //--------------------------
    public void update() {
        // move
        x += dir.x * speed * Time.deltaTime;

        // checkCollision with player
        checkCollision();
        animation();

        // initialize when out of screen
        if (x < -r * 4 || x > scrW + r * 4) {
            init();
        }
    }

    //--------------------------
    // checkCollision
    //--------------------------
    private void checkCollision() {
        // collision with player
        if (GameView.player.checkCollision(x, y, r / 2)) {
            dir.x = -dir.x;
        }
    }

    private void animation() {
        aniTime += Time.deltaTime;

        if (aniTime > aniSpan) {  // if aniSpan bigger, FPS become smaller
            aniTime = 0;
            aniNum = MathF.repeat(aniNum, 4);  // CultBird consist of 4 images

            if (dir.x == -1) {
                // leftSide images
                img = arImg[0][aniNum];
            } else if (dir.x == 1) {
                // rightSide images
                img = arImg[1][aniNum];
            } else {
                // prevent nullPointException
                img = arImg[0][aniNum];
            }
        }
    }

    //--------------------------
    // make Bitmap
    //--------------------------
    private void makeBitmap(Context context) {
        // CultBird
        Bitmap tmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.cultbird);
        w = tmp.getWidth() / 4;
        h = tmp.getHeight() / 2;
        r = w / 2;  // for checkCollision - Circle : Rectangle

        //leftSide(<--) images
        for (int i = 0; i < 4; i++) {
            arImg[0][i] = Bitmap.createBitmap(tmp, w * i, 0, w, h);
        }

        // rightSide(-->) images
        for (int i = 0; i < 4; i++) {
            arImg[1][i] = Bitmap.createBitmap(tmp, w * i, h, w, h);
        }
    }

    //--------------------------
    // initialize
    //--------------------------
    private void init() {
        Random rnd = new Random();
        // move direction - leftSide & rightSide
        if (rnd.nextInt(2) == 1) {
            dir.x = 1;
            x = -r * 4;
        } else {
            dir.x = -1;
            x = scrW + r * 4;
        }

        // position & speed
        y = rnd.nextInt(11) + 35;        // 35~45
        speed = rnd.nextInt(300) + 300;  // 300~600
        dir.y = 0;
    }
}
