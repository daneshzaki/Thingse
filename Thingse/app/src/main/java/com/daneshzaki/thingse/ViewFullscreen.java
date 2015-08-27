package com.daneshzaki.thingse;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.util.Log;
import android.view.Display;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Full-screen activity that displays the image full screen
 *
 */
public class ViewFullscreen extends Activity implements View.OnTouchListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.i("ViewFullScreen", "onCreate");

        this.getWindow().setStatusBarColor(Color.BLACK);
        this.getWindow().setNavigationBarColor(Color.BLACK);


        setContentView(R.layout.activity_view_fullscreen);

        //get values of the selected thing into the bundle
        bundle = this.getIntent().getBundleExtra("thing");


        //load the image
        loadValues();
    }


    //fill fields with selected thing's values
    private void loadValues()
    {

        if (bundle == null)
        {
            return;
        }

        Log.i("ViewFullScreen", "bundle is " + bundle.toString());

        ImageView thingImage = ((ImageView)findViewById(R.id.thingImage));
        thingImage.setOnTouchListener(this);
        //scale and set pic
        if(bundle.getString("picLocation")!= null && bundle.getString("picLocation").trim().length()>0)
        {
            BitmapFactory.Options options = new BitmapFactory.Options();

            Bitmap bmp = BitmapFactory.decodeFile(bundle.getString("picLocation"), options);

            //get display size
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;

            bmp = Bitmap.createScaledBitmap(bmp, width, height, true);

            Drawable d = new BitmapDrawable(getResources(), bmp);
            thingImage.setImageDrawable(d);

        }
        else
        {
            //set a default image if there is no associated pic with this thing
            thingImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.1f));

            thingImage.setImageResource(R.drawable.ic_launcher);

        }

    }

    public boolean onTouch(View view, MotionEvent motionEvent)
    {
        finish();
        return false;
    }

    //bundle for passing the bitmap
    private Bundle bundle = null;

}
