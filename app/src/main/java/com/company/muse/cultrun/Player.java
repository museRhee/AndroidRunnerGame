package com.company.muse.cultrun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;

/**
 * Set player's images, position, direction, animation...
 */

public class Player {
    // status code
    private enum STATE {
        RUN, JUMP, HIT, FALL, STOP
    }

    private STATE state;

    // screen size, angerCnt
    private int scrW, scrH;
    public int angerCnt;    // increase when occur collision

    // original Bitmap, Bitmap, size
    private Bitmap[][] arImg = new Bitmap[5][6];
    public Bitmap img;  // <-- GameView.onDraw
    public int w, h;

    // position, direction
    public float x, y;                  // current position
    private float tx, ty;               // target(destination) position
    public PointF dir = new PointF();   // move direction

    // speed, gravity, ground
    private float moveSpeed = 600;
    private float currSpeed;
    private float speedJump = 1600;
    private float gravity = 2000;
    public float ground;

    // animation, time
    private int aniNum = 0;
    private float aniSpan = 0.10f;      // control FPS(Frames per seconds)
    private float aniTime;
    private float waitTime = 0.5f;

    //--------------------------
    // Constructor
    //--------------------------
    public Player(Context context, int width, int height) {
        scrW = width;
        scrH = height;

        // Bitmap, initialize
        makeBitmap(context);
        initPlayer();
    }

    //--------------------------
    // update <-- Thread
    //--------------------------
    public void update() {
        switch (state) {
            case STOP:
                currSpeed = 0;
                break;
            case RUN:
                currSpeed = moveSpeed;
                break;
            case HIT:  // collision with Cult(obstacle)
                setHit();
                break;
            case FALL: // HIT then FALL
                setFall();
                break;
        }

        movePlayer();
        checkGround();
        checkDestn();
        animation();
    }

    //--------------------------
    // move player
    //--------------------------
    private void movePlayer() {
        // gravity
        dir.y += gravity * Time.deltaTime;

        // move
        x += dir.x * currSpeed * Time.deltaTime;
        y += dir.y * Time.deltaTime;
    }

    //--------------------------
    // check the collision of ground
    //--------------------------
    private void checkGround() {
        if (y > ground - h) {
            y = ground - h;
            dir.y = 0;

            if (state == STATE.JUMP) {
                state = STATE.STOP;
            }
        }
    }

    //--------------------------
    // check destination position
    //--------------------------
    private void checkDestn() {
        // stop when arrive in destination position
        if (state == STATE.RUN && Math.abs(x - tx) < 2) {
            state = STATE.STOP;
        }

        // player always in the screen
        if (x < w) {
            x = w;
            state = STATE.STOP;
        }

        if (x > scrW - w) {
            x = scrW - w;
            state = STATE.STOP;
        }
    }

    //--------------------------
    // animation
    //--------------------------
    private void animation() {
        aniTime += Time.deltaTime;

        if (aniTime > aniSpan) {  // if aniSpan bigger, FPS become smaller
            aniTime = 0;

            // set aniNum by state
            if (state == STATE.RUN || state == STATE.STOP) {
                aniNum = MathF.repeat(aniNum, 6); // RUN consist of 6 images

            } else if (state == STATE.JUMP) {
                aniNum = MathF.repeat(aniNum, 5);
                if (aniNum == 0) aniNum = 4;      // prevent nullPointException

            } else if (state == STATE.HIT) {
                aniNum = MathF.repeat(aniNum, 5);

            } else {
                aniNum = MathF.repeat(aniNum, 2);
            }
        }

        img = arImg[state.ordinal()][aniNum];
    }

    //----------------------------
    // run when collide with Cult
    //----------------------------
    private void setHit() {
        if (state != STATE.JUMP) {
            currSpeed = 0;
        }

        // run setFall after waitTime
        waitTime -= Time.deltaTime;
        if (waitTime <= 0) {
            state = STATE.FALL;
            waitTime = 1f;
        }
    }

    //--------------------------
    // run after setHit method
    //--------------------------
    private void setFall() {
        currSpeed = 0;

        // become STOP after waitTime
        waitTime -= Time.deltaTime;
        if (waitTime <= 0) {
            waitTime = 0.5f;
            state = STATE.STOP;
        }
    }

    //--------------------------
    // make Bitmap images
    //--------------------------
    private void makeBitmap(Context context) {
        // player
        Bitmap tmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.player);
        w = tmp.getWidth() / 6;
        h = tmp.getHeight() / 4;

        // STATE
        for (int i = 0; i < 6; i++) {
            arImg[0][i] = Bitmap.createBitmap(tmp, w * i, 0, w, h);     // RUN
            arImg[1][i] = Bitmap.createBitmap(tmp, w * i, h, w, h);     // JUMP
            arImg[2][i] = Bitmap.createBitmap(tmp, w * i, h * 2, w, h); // HIT
            arImg[3][i] = Bitmap.createBitmap(tmp, w * i, h * 3, w, h); // FALL
            arImg[4][i] = Bitmap.createBitmap(tmp, w * i, 0, w, h);     // STOP
        }

        w /= 2;
        h /= 2;
    }

    //--------------------------
    // initialize Player
    //--------------------------
    private void initPlayer() {
        // move direction
        dir.x = 1;  // -->
        dir.y = 0;

        // ground position
        ground = scrH * 0.9f;

        // target&current position
        tx = x = scrW / 2;
        ty = y = ground - h;

        state = STATE.STOP;
    }

    //--------------------------
    // setAction <-- Touch Event
    //--------------------------
    public void setAction(float tx, float ty) {
        // can JUMP when RUN, STOP
        if (state != STATE.RUN && state != STATE.STOP) return;

        // touch player, then JUMP
        if (MathF.hitTest(x, y, w, tx, ty)) {
            aniNum = 0; // JUMP images start from 0 index
            dir.y = -speedJump;
            state = STATE.JUMP;
            // touch space, player move
        } else {
            // touch space == target position
            this.tx = tx;
            this.ty = y;

            // set move direction
            dir.x = x < tx ? 1 : -1;
            state = STATE.RUN;
        }
    }

    //-------------------------------------------------
    // checkCollision <-- CultGreen, CultRed, CultBird
    //-------------------------------------------------
    public boolean checkCollision(float tx, float ty, int r) {
        boolean hit = false;

        // prevent continuous collision
        if (state != STATE.HIT && state != STATE.FALL) {
            // circle:rectangle collision
            if (MathF.checkCollision(tx, ty, r, x, y, w * 0.7f, h * 0.5f)) {
                angerCnt += 1;
                state = STATE.HIT;
                hit = true;
            }
        }
        return hit;
    }
}
