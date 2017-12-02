package com.company.muse.cultrun;

/**
 * Contains collision checking and repeat number methods
 */

public class MathF {

    //----------------------------------------
    // Hit test - Is touch position in circle?
    //----------------------------------------
    static public boolean hitTest(float x, float y, float r, float tx, float ty) {
        return (x - tx) * (x - tx) + (y - ty) * (y - ty) < r * r;
    }

    //----------------------------------------
    // Check collision - circle : rectangle
    //----------------------------------------
    static public boolean checkCollision(float x, float y, float r, float tx, float ty, float tw, float th) {
        return Math.abs(x - tx) <= (tw + r) && Math.abs(y - ty) <= (th + r);
    }

    //----------------------------------------
    // Repeat until the end number
    //----------------------------------------
    static public int repeat(int n, int end) {
        if (++n >= end) n = 0;
        return n;
    }
}