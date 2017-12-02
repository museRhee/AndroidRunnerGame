package com.company.muse.cultrun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;

import java.util.Random;

/**
 * Setting Obstacle_CultGreen's images, position, direction, animation...
 */

public class CultGreen {
    // screen, CultGreen size
    private int scrW, scrH;
    public int w, h;

    // current position, radius
    public float x, y;
    public int r;

    // speed, gravity, ground
    private float speed;
    private final float GRAVITY = 800;
    public float ground;

    // move direction, animation, bitmap
    private PointF dir = new PointF();
    private int aniNum = 0;
    private float aniSpan = 0.10f;   // control FPS
    private float aniTime;
    private Bitmap[][] arImg = new Bitmap[5][4];
    public Bitmap img;  // <-- GameView.onDraw

    private boolean firstSee = true; // firstTime setting

    //--------------------------
    // Constructor
    //--------------------------
    public CultGreen(Context context, int width, int height) {
        scrW = width;
        scrH = height;

        // ground height
        ground = scrH * 0.9f;

        // bitmap, initialize
        makeBitmap(context);
        init();
    }

    //--------------------------
    // Move
    //--------------------------
    public void update() {
        // gravity
        dir.y += GRAVITY * Time.deltaTime;

        // move
        x += dir.x * speed * Time.deltaTime;
        y += dir.y * Time.deltaTime;

        // check collision with ground&player
        checkGround();
        checkCollision();
        animation();

        // initialize when out of screen
        if (x < -r * 4 || x > scrW + r * 4) {
            init();
        }
    }

    //--------------------------
    // check collision with ground
    //--------------------------
    private void checkGround() {
        if (y > ground - r) {
            y = ground - r;
            dir.y = -dir.y;   // change direction of Y
        }
    }

    //--------------------------
    // check collision
    //--------------------------
    private void checkCollision() {
        // collision with player
        if (GameView.player.checkCollision(x, y, r / 2)) {
            dir.x = -dir.x;
        }
    }

    //--------------------------
    // animation
    //--------------------------
    private void animation() {
        aniTime += Time.deltaTime;

        if (aniTime > aniSpan) {  // if aniSpan bigger, FPS become smaller
            aniTime = 0;
            aniNum = MathF.repeat(aniNum, 4);  // CultRed consist of 4 images

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
        // GultGreen
        Bitmap tmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.cultgreen);
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
        if (rnd.nextInt(7) == 1) {
            dir.x = 1;
            x = -r * 4;
        } else {
            dir.x = -1;
            x = scrW + r * 4;
        }

        // position & speed
        y = rnd.nextInt(301) + 800;         // 800~1100
        speed = rnd.nextInt(301) + 500;     // 600~800
        dir.y = 0;

        // firstTime setting
        if (firstSee) {
            firstSee = false;
            dir.x = 1;
            x = -r * 4;
            y = 800;
            speed = 300;
        }
    }
}
