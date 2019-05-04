package com.example.remark.api;

import android.os.SystemClock;
import android.view.View;

abstract public class PreventDoubleClickOnClickListener implements View.OnClickListener {
    private static final int CLICK_INTERVAL = 1000;
    private long lastClickTime;

    abstract public void preventDoubleClickOnClick(View view);

    @Override
    public void onClick(View view) {
        //do nothing if two click interval less than CLICK_INTERVAL
        if (SystemClock.elapsedRealtime() - lastClickTime < CLICK_INTERVAL)
            return;

        //save the click time of this click
        lastClickTime = SystemClock.elapsedRealtime();

        preventDoubleClickOnClick(view);
    }
}
