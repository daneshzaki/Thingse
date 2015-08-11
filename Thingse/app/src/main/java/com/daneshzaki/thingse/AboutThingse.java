package com.daneshzaki.thingse;

import android.app.Activity;
import android.os.Bundle;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;


public class AboutThingse extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        this.getWindow().setStatusBarColor(Color.parseColor("#33B5E5"));
        this.getWindow().setNavigationBarColor(Color.parseColor("#fff3f3f3"));

        setContentView(R.layout.activity_about_thingse);


        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP|ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33B5E5")));
        actionBar.setTitle(" ");
        actionBar.setHomeButtonEnabled(true);

    }


}
