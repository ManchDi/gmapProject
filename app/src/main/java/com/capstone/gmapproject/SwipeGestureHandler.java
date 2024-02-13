package com.capstone.gmapproject;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class SwipeGestureHandler extends GestureDetector.SimpleOnGestureListener {

    private static final int SWIPE_THRESHOLD = 200;

    private final MainActivity mainActivity;

    public SwipeGestureHandler(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public boolean onDown(MotionEvent event) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
        float deltaY = event2.getRawY() - event1.getRawY();

        if (Math.abs(deltaY) > SWIPE_THRESHOLD) {
            if (deltaY > 0) {
                // Swipe down
                mainActivity.restoreWindow();
            } else {
                // Swipe up
                mainActivity.expandWindow();
            }
            return true;
        }
        return false;
    }
}
