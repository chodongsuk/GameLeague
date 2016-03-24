package com.kr.gameleague;

import android.app.Application;

import com.buzzvil.buzzscreen.sdk.BuzzScreen;
import com.buzzvil.buzzscreen.sdk.SimpleLockerActivity;

/**
 * Created by Administrator on 2016-03-15.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BuzzScreen.init("70332319406640", this, SimpleLockerActivity.class, R.drawable.image_on_fail, false);

    }
}
