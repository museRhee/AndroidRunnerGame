package com.company.muse.cultrun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

/**
 * Draw background using Images(far, near)
 */

public class BackGround {
    // screen size
    private int w, h;

    // background scroll speed
    private int speedNear = 650;
    private int speedFar = 350;

    // near&far background Bitmap, image count
    private Bitmap[] near = new Bitmap[2];
    private Bitmap[] far = new Bitmap[2];
    private int nearCnt = 2;
    private int farCnt = 2;

    // moving direction, near&far offset
    private int dir;
    private float ofsNear;
    private float ofsFar;

    // near&far image number
    private int nearNum1, nearNum2;
    private int farNum1, farNum2;

    //--------------------------
    // constructor
    //--------------------------
    public BackGround(Context context, int width, int height) {
        w = width;
        h = height;

        makeBitmap(context);
    }

    //--------------------------
    // update <-- Thread
    //--------------------------
    public void update() {
        scrollFar();
        scrollNear();
    }

    //--------------------------
    // draw <-- Ondraw
    //--------------------------
    public void draw(Canvas canvas) {
        // far background
        canvas.drawBitmap(far[farNum1], -ofsFar, 0, null);
        if (farNum2 == 2) {  // prevent error:IndexOutOfRange
            farNum2 = 1;
            canvas.drawBitmap(far[farNum2], w - ofsFar, 0, null);
        } else {
            canvas.drawBitmap(far[farNum2], w - ofsFar, 0, null);
        }

        // near background
        canvas.drawBitmap(near[nearNum1], -ofsNear, h * 2 / 3, null);
        if (nearNum2 == 2) {  // prevent error:IndexOutOfRange
            nearNum2 = 1;
            canvas.drawBitmap(near[nearNum2], w - ofsNear, h * 2 / 3, null);
        } else {
            canvas.drawBitmap(near[nearNum2], w - ofsNear, h * 2 / 3, null);
        }
    }

    //--------------------------
    // scroll far background
    //--------------------------
    private void scrollFar() {
        dir = 1;  // moving(scrolling) direction
        ofsFar += dir * speedFar * Time.deltaTime;

        if (ofsFar > w) {
            ofsFar -= w;
            farNum1 = MathF.repeat(farNum1++, farCnt);
        }

        farNum2 = farNum1 + 1;
        if (farNum2 >= farCnt) farNum2 = 0;
    }

    //--------------------------
    // scroll near background
    //--------------------------
    private void scrollNear() {
        ofsNear += dir * speedNear * Time.deltaTime;

        if (ofsNear > w) {
            ofsNear -= w;
            nearNum1 = MathF.repeat(nearNum1++, nearCnt);
        }

        nearNum2 = nearNum1 + 1;
        if (nearNum2 >= nearCnt) nearNum2 = 0;
    }

    //--------------------------
    // make Bitmap
    //--------------------------
    private void makeBitmap(Context context) {
        // far background
        for (int i = 0; i < 2; i++) {
            Bitmap tmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.far0 + i);
            far[i] = Bitmap.createScaledBitmap(tmp, w, (int) (h * 0.7f), true);
        }

        // near background
        for (int i = 0; i < 2; i++) {
            Bitmap tmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.near0 + i);
            near[i] = Bitmap.createScaledBitmap(tmp, w, h / 2, true);
        }
    }
}
