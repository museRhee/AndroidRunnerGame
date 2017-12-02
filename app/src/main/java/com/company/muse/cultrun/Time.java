package com.company.muse.cultrun;

/**
 * Get currentTime and deltaTime
 */

public class Time {
    static private long currentTime = System.nanoTime();
    static public float deltaTime;  // elapsed time from Ex-Frame

    //-----------------------------
    // deltaTime calculation
    //-----------------------------
    static public void update() {
        deltaTime = (System.nanoTime() - currentTime) / 1000000000f;
        currentTime = System.nanoTime();
    }
}