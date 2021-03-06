package com.daneshzaki.thingse;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.app.ActionBar;
import android.view.MenuItem;
import android.widget.Toast;

//This class corresponds to the View screen that is called when a "Thing" is selected from the main screen
public class ViewSomething extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
		this.getWindow().setNavigationBarColor(Color.parseColor("#D0D0D0"));

		setContentView(R.layout.activity_view_something);

        //set values of the selected thing in the input fields
        bundle = this.getIntent().getBundleExtra("thing");

		Log.i("ViewSomething", "Before loadValues Bundle is "+bundle);

		//load the thing values on the screen
        loadValues();

		Log.i("ViewSomething", "After loadValues Bundle contains " + bundle.getParcelable("imageBitmap"));

	}

	//call ViewFullScreen Activity to view thing full screen
	public void viewThingFull(View v)
	{
		Log.i("ViewSomething", "*** viewThingFull ***");

		//do not open full screen if there is no image
		if(bundle.getString("picLocation")== null || bundle.getString("picLocation").trim().length() == 0)
		{
			//show Toast
			Toast.makeText(getBaseContext(), "No pic associated with this thing", Toast.LENGTH_SHORT).show();
			return;
		}

		//create an intent and add the bundle to it
		Intent viewFullIntent = new Intent(this, ViewFullscreen.class);
		Log.i("ViewSomething", "*** viewThingFull intent created");

		viewFullIntent.putExtra("thing", bundle);
		Log.i("ViewSomething", "calling ViewFullScreen class with bundle="+bundle);

		startActivity(viewFullIntent);

	}

    //call EditSomething Activity to edit thing 
    public void editThing()
    {
    	Log.i("ViewSomething", "editThing entry bundle price = " + bundle.getString("price"));
    	
    	Log.i("ViewSomething", "editThing entry thing id = "+bundle.getString("id"));

    	//create an intent and add the bundle to it
    	Intent editIntent= new Intent(this, EditSomething.class);

    	editIntent.putExtra("thing", bundle);
        
    	startActivity(editIntent);

    }
    
    //delete the thing from db
    public void deleteThing()
    {
    	Log.i("ViewThing", "deleteThing entry");
    	
    	//use SQL adapter to access db 
    	final SQLAdapter adapter = new SQLAdapter(this);
    	
    	final Intent mainIntent = new Intent(this, ThingseActivity.class);
    	
    	//display confirmation
    	 new AlertDialog.Builder(ViewSomething.this)                
                .setTitle("Delete "+bundle.getString("name")+"?")
                .setInverseBackgroundForced(true)
                .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    	Log.i("ViewThing", "deleteThing ok clicked");
                    	
                    	//delete rec in db                    	
                    	//open the db through the adapter
                    	adapter.open();
                    	
                    	//delete the details                    	
                    	boolean status = adapter.deleteThing(Long.valueOf(bundle.getString("id")));                    	 

                    	Log.i("ViewThing", "Record with id = "+ bundle.getString("id") +" deleted status = "+status);
                    	
                    	//close the db
                    	adapter.close();
                    	
                    	//sending back to the main activity

                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);                    	
                    }
                })
                .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    	Log.i("ViewThing", "deleteThing cancel clicked");
                    	
                    }
                }).create().show();
    	
    }

	//menu items for edit and delete
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add("Edit").setIcon(R.drawable.ic_mode_edit_black_18dp)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		menu.add("Delete").setIcon(R.drawable.ic_delete_black_18dp)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		return true;

	}
    //handle view, edit and delete
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem)
	{
		Log.i("ViewSomething","MenuItem="+menuItem);
		Log.i("ViewSomething", "MenuItem title=" + menuItem.getTitle());

		// edit
		if (menuItem.getTitle() != null && menuItem.getTitle().equals("Edit"))
		{
			Log.i("ViewSomething","edit called");
			editThing();
		}

		// delete
		else if (menuItem.getTitle() != null && menuItem.getTitle().equals("Delete"))
		{
			Log.i("ViewSomething","del called");
			deleteThing();
		}

		//back
		else if (menuItem.getItemId() == android.R.id.home)
		{
			Log.i("ViewSomething","back called");
			//create an intent and add the bundle to it
			Intent parentIntent= new Intent(this, ThingseActivity.class);

			parentIntent.putExtra("thing", bundle);

			startActivity(parentIntent);
		}

		return super.onOptionsItemSelected(menuItem);
	}

    //fill fields with selected thing's values
    private void loadValues()
    {
		//gift label
		String giftLabel = "";

		//currency label
		String currencyLabel = "";

    	//set thing name   
    	Log.i("ViewSomething", "Bundle is " + bundle);

		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33B5E5")));
		actionBar.setTitle(bundle.getString("name"));
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

    	//set label for gift
    	if(bundle.getString("price").trim().startsWith("Gift :"))
    	{
    		giftLabel = "Received on ";
			((TextView)findViewById( R.id.priceValue)).setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_gift,0,0,0);
		}
		else
		{
			giftLabel = "Purchased on ";

			//set currency chosen by user
			currencyLabel = sharedPrefs.getString("currencyPref", "Rs.");
			((TextView)findViewById( R.id.priceValue)).setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_price,0,0,0);
		}
    	
    	//set price
		((TextView)findViewById( R.id.priceValue)).setText((currencyLabel + bundle.getString("price")).trim());

    	//set purchase date
    	((TextView)findViewById( R.id.datePurchValue)).setText(giftLabel + bundle.getString("purchaseDate"));
    	
    	//set description
    	if(bundle.getString("description")!= null && bundle.getString("description").trim().length()>0)
    	{
    		((TextView)findViewById( R.id.descValue)).setText(bundle.getString("description"));
    	}
    	else
    	{
    		((TextView)findViewById( R.id.descValue)).setText("One of the many things that I have");
    	}

		//set fonts for all text
		Typeface typeface = Typeface.createFromAsset( getResources().getAssets(), "SourceSansPro-Regular.otf");
		((TextView)findViewById( R.id.datePurchValue)).setTypeface(typeface);
		((TextView)findViewById( R.id.priceValue)).setTypeface(typeface);
		((TextView)findViewById( R.id.descValue)).setTypeface(typeface);
		ImageView thingImage = ((ImageView)findViewById(R.id.thingImage));

    	//scale and set pic
		if(bundle.getString("picLocation")!= null && bundle.getString("picLocation").trim().length() > 0)
    	{
			BitmapFactory.Options options = new BitmapFactory.Options();

			options.inSampleSize = 4;
			//9-Sep-2015 scale large images
			options.inJustDecodeBounds = true;

			options.inDither = false;

			if (options.outWidth > 3000 || options.outHeight > 2000)
			{
				options.inSampleSize = 6;
			} else if (options.outWidth > 2000 || options.outHeight > 1500)
			{
				options.inSampleSize = 5;
			} else if (options.outWidth > 1000 || options.outHeight > 1000)
			{
				options.inSampleSize = 3;
			}
			options.inJustDecodeBounds = false;

			Bitmap bmp = BitmapFactory.decodeFile(bundle.getString("picLocation"), options);

			//get display size
			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int width = size.x;
			int height = size.y;

			//9-Sep-2015 scale large images
			Log.i("ViewSomething", "***display width="+width);
			Log.i("ViewSomething", "***display height="+height);

			Log.i("ViewSomething", "***bmp width="+bmp.getWidth());
			Log.i("ViewSomething", "***bmp height="+bmp.getHeight());

			//handle wide images
			int ratio = bmp.getWidth() / bmp.getHeight();

			if (ratio <=0)
			{
				ratio = 1;
			}

			bmp = Bitmap.createScaledBitmap(bmp,
					(int) (height * ratio* 0.5), (int)(height *0.5), false);

			Drawable d = new BitmapDrawable(getResources(), bmp);
			thingImage.setImageDrawable(d);
			thingImage.setBackgroundColor(Color.LTGRAY);

    	}
    	else
    	{
    		//set default image if there is no associated pic with this thing
			thingImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.1f));
			thingImage.setImageResource(R.drawable.ic_launcher);
			thingImage.setBackgroundColor(Color.LTGRAY);
    	}
    	
    }
    
    //bundle for use here and for passing to edit activity
    private Bundle bundle = null;
}
