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
    	}
		else
		{
			giftLabel = "Purchased on ";

			//set currency chosen by user
			currencyLabel = sharedPrefs.getString("currencyPref", "Rs.");
		}
    	
    	//set price
		((TextView)findViewById( R.id.priceValue)).setText((currencyLabel + bundle.getString("price")).trim());

    	//set purchase date
    	((TextView)findViewById( R.id.datePurchValue)).setText(giftLabel + bundle.getString("purchaseDate"));
    	
    	//set description
    	if(bundle.getString("description")!= null)
    	{
    		((TextView)findViewById( R.id.descValue)).setText(bundle.getString("description"));
    	}
    	else
    	{
    		((TextView)findViewById( R.id.descValue)).setText(" ");
    	}
    	
    	//scale and set pic 
    	if(bundle.getString("picLocation")!= null && bundle.getString("picLocation").trim().length()>0)
    	{
			BitmapFactory.Options options = new BitmapFactory.Options();

			options.inSampleSize = 4;

			options.inDither = true;

			//Jul-15 final int THUMBNAIL_SIZE = 64;
			/*final int THUMBNAIL_SIZE = 128;

			Bitmap bmp = BitmapFactory.decodeFile(bundle.getString("picLocation"), options);

			Log.i("ViewSomething", "bmp.getWidth() = "+bmp.getWidth());

			Log.i("ViewSomething", "bmp.getHeight() = " + bmp.getHeight());

			int ratio = bmp.getWidth() / bmp.getHeight();

			if (ratio <=0)
			{
				ratio = 1;
			}

			bmp = Bitmap.createScaledBitmap(bmp,
					(int) (THUMBNAIL_SIZE * ratio), THUMBNAIL_SIZE, false);

*/
			Bitmap bmp = BitmapFactory.decodeFile(bundle.getString("picLocation"), options);

			//get display size
			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int width = size.x;
			int height = size.y;

			bmp = Bitmap.createScaledBitmap(bmp, width, 600, true);

			Drawable d = new BitmapDrawable(getResources(), bmp);

			ImageView thingImage = ((ImageView)findViewById(R.id.thingImage));

			//thingImage.setScaleX(0.75f);
			//thingImage.setScaleY(0.75f);
			thingImage.setImageDrawable(d);

    	}
    	else
    	{
    		//set a one pixel image if there is no associated pic with this thing
			ImageView thingImage = ((ImageView)findViewById(R.id.thingImage));
			thingImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.1f));

			//((ImageView)findViewById(R.id.thingImage)).setImageResource(R.drawable.onepixel);

    	}
    	
    }
    
    //bundle for use here and for passing to edit activity
    private Bundle bundle = null;



}
