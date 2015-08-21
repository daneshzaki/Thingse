package com.daneshzaki.thingse;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.TextView;


public class AboutThingse extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        this.getWindow().setStatusBarColor(Color.parseColor("#33B5E5"));
        this.getWindow().setNavigationBarColor(Color.parseColor("#fff3f3f3"));

        setContentView(R.layout.activity_about_thingse);
        //set fonts for all text
        Typeface typeface = Typeface.createFromAsset( getResources().getAssets(), "SourceSansPro-Regular.otf");
        ((TextView)findViewById( R.id.title)).setTypeface(typeface, Typeface.BOLD);
        ((TextView)findViewById( R.id.description)).setTypeface(typeface);
        ((TextView)findViewById( R.id.copyright)).setTypeface(typeface);
        ((TextView)findViewById( R.id.version)).setTypeface(typeface);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP|ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33B5E5")));
        actionBar.setTitle(" ");
        actionBar.setHomeButtonEnabled(true);

    }


}
