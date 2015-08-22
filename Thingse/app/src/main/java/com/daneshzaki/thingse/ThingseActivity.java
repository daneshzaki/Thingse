package com.daneshzaki.thingse;

import java.util.ArrayList;


import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
//import android.support.design.widget.FloatingActionButton;
import com.etsy.android.grid.StaggeredGridView;
import com.melnykov.fab.*;

//This class corresponds to the main screen that is displayed when the Thingse application is opened
//This class displays the list of things from the database and serves as the navigation point to other screens
public class ThingseActivity extends Activity implements AdapterView.OnItemClickListener
{

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		this.getWindow().setNavigationBarColor(Color.parseColor("#D0D0D0"));

		setContentView(R.layout.activity_thingse);
		Log.i("ThingseActivity", "ThingseActivity onCreate");

		// get the grid view for this activity
		StaggeredGridView thingsList = (StaggeredGridView)findViewById(R.id.grid_view);

		thingsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		thingsList.setTextFilterEnabled(true);

		ActionBar actionBar = getActionBar();

		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33B5E5")));

		// retrieving things from db
		ArrayList<String[]> dispThingsArList = getThings();

		// get name Arr
		final String[] nameArr = dispThingsArList.get(0);

		// get price Arr
		final String[] priceArr = dispThingsArList.get(1);

		// get purchDate Arr
		final String[] purchDateArr = dispThingsArList.get(2);

		// create an adapter with custom style for text
		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_checked, nameArr)
		{

			// layout for image and two text views
			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
				ImageView iv;
				TextView tv1;

				FrameLayout ll;

				//set fonts for all text
				Typeface typeface = Typeface.createFromAsset( getResources().getAssets(), "SourceSansPro-Regular.otf");

				if (convertView == null)
				{
					iv = new ImageView(getContext());
					iv.setAdjustViewBounds(true);
					//iv.setPadding(5, 10, 5, 10);
					iv.setPadding(20, 20, 20, 20);
					//uncomment the line below if thumbnails are of different sizes
					//iv.setScaleType(ImageView.ScaleType.CENTER_CROP);

					tv1 = new TextView(getContext());
					tv1.setTypeface(typeface, Typeface.BOLD);
					tv1.setGravity(Gravity.BOTTOM);
					tv1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

					tv1.setPadding(20, 20, 20, 22);
					tv1.setTextSize(16.0f);
					tv1.setTextColor(android.graphics.Color.parseColor("#fff3f3f3"));
					tv1.setText(nameArr[position]);

					ll = new FrameLayout(getContext());
					ll.setBackgroundColor(android.graphics.Color.parseColor("#fff3f3f3"));
					// display thing image
					displayThingImage(iv, position);

					ll.addView(iv);
					ll.addView(tv1);

				} else
				{
					ll = (FrameLayout) convertView;
					iv = (ImageView) ll.getChildAt(0);
					tv1 = (TextView) (ll.getChildAt(1));
					tv1.setTypeface(typeface, Typeface.BOLD);
					// display thing image
					displayThingImage(iv, position);
					tv1.setText(nameArr[position]);
				}

				return ll;
			}
		};

		thingsList.setAdapter(listAdapter);
		thingsList.setOnItemClickListener(this);

		//fab code
		final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setHovered(true);
		fab.attachToListView(thingsList, new ScrollDirectionListener() {
			@Override
			public void onScrollDown() {
				//Log.d("ThingseActivity", "onScrollDown()");
				fab.show();
			}

			@Override
			public void onScrollUp() {
				//Log.d("ThingseActivity", "onScrollUp()");
				fab.show();

			}
		}, new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				Log.d("ThingseActivity", "onScrollStateChanged()");
				fab.show();

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				//Log.d("ThingseActivity", "onScroll()");
				fab.show();

			}
		});


		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Code to Add an item
				Log.i("ThingseActivity", "FAB clicked");
				startActivity(new Intent(ThingseActivity.this, AddSomething.class));
			}
		});

		Log.i("ThingseActivity", "ThingseActivity exit onCreate");
	}

	// handle row selects
	public void onItemClick(AdapterView<?> adapterView, View v, int position, long id)
	{
		// call display thing with values of selected object

		// create a bundle with values to be passed to display screen
		Bundle bundle = new Bundle();

		// thing id
		bundle.putString("id", thingsArList.get(position).getId());

		// thing name
		bundle.putString("name", thingsArList.get(position).getName());

		// thing price
		bundle.putString("price", thingsArList.get(position).getPrice());

		// thing date
		bundle.putString("purchaseDate", thingsArList.get(position)
				.getPurchaseDate());

		// thing description
		bundle.putString("description", thingsArList.get(position)
				.getDescription());

		// thing pic location
		bundle.putString("picLocation", thingsArList.get(position)
				.getPicLocation());
		Log.i("ThingseActivity",
				"onListItemClick setting bundle pic location = "
						+ bundle.getString("picLocation"));

		// create an intent and add the bundle to it
		Intent displayIntent = new Intent(ThingseActivity.this,
				ViewSomething.class);

		displayIntent.putExtra("thing", bundle);

		startActivity(displayIntent);
		
		
	}
	
	//handle back button
	@Override
	public void onBackPressed()
	{
		Log.i("ThingseActivity","onBackPressed");
		
		 Intent intent = new Intent(Intent.ACTION_MAIN); 
		 intent.addCategory(Intent.CATEGORY_HOME); 
		 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		 startActivity(intent); 
		 finish();
	}

	// call addSomething Activity to add things
	public void callAddSomething(View v)
	{
		Log.i("ThingseActivity", "callAddSomething");

		startActivity(new Intent(ThingseActivity.this, AddSomething.class));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add("Preferences").setIcon(android.R.drawable.ic_menu_manage)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		// menu.add(Menu.NONE, 0, 0, "Preferences");

		//menu.add("Add").setIcon(android.R.drawable.ic_menu_manage)
		//		.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem)
	{
		// show prefs
		if (menuItem.getTitle().equals("Preferences"))
		{
			startActivity(new Intent(this, Preferences.class));

		}

		// call add something activity
		if (menuItem.getTitle().equals("Add"))
		{
			startActivity(new Intent(ThingseActivity.this, AddSomething.class));
		}

		return true;
	}

	// display thing image
	private void displayThingImage(ImageView imageView, int position)
	{
		// get pic
		Drawable thingImage = thingsArList.get(position).getPic();

		if (thingImage != null)
		{
			imageView.setImageDrawable(thingImage);
		} else
		{
			// set default pic if there is no associated pic with this thing
			imageView.setImageResource(R.drawable.onepixel);

		}

	}

	// get thing image from path
	private Drawable getThingImage(String thingImageLocation)
	{
		// pic location can be null
		Drawable d = null;

		if (thingImageLocation != null
				&& thingImageLocation.trim().length() > 0 && !thingImageLocation.trim().equalsIgnoreCase("null"))
		{
			BitmapFactory.Options options = new BitmapFactory.Options();

			options.inSampleSize = 6;

			options.inPurgeable = true;

			options.inDither = false;

			//Aug 15 2015 final int THUMBNAIL_SIZE = 64;
			final int THUMBNAIL_SIZE = 256;

			Bitmap bmp = BitmapFactory.decodeFile(thingImageLocation, options);

			Log.i("ThingseActivity", "bmp.getWidth() = "+bmp.getWidth());
			
			Log.i("ThingseActivity", "bmp.getHeight() = "+bmp.getHeight());			
			
			int ratio = bmp.getWidth() / bmp.getHeight();

			if (ratio <=0)
			{
				ratio = 1;
			}

			bmp = Bitmap.createScaledBitmap(bmp,
					(int) (THUMBNAIL_SIZE * ratio), THUMBNAIL_SIZE, false);

			d = new BitmapDrawable(getResources(), bmp);

		}

		return d;
	}

	// get list of things from db
	private ArrayList<String[]> getThings()
	{
		Log.i("ThingseActivity", "ThingseActivity getThings");

		// store thing objects for passing to other screens
		thingsArList = new ArrayList<Thing>();

		// store display strings
		ArrayList<String[]> dispThingsArList = new ArrayList<String[]>();

		// use SQLAdapter to get list of things from db
		SQLAdapter adapter = new SQLAdapter(this);

		adapter.open();

		// adapter returns a db cursor with the records
		Cursor cursor = adapter.getAllThings();

		// initialize display arrays
		String[] nameArr = new String[0];
		String[] priceArr = new String[0];
		String[] purchDateArr = new String[0];

		// navigate through cursor to retrieve all records starting from the
		// first
		if (cursor != null && cursor.moveToFirst())
		{
			// reset arrays' size for storing thing name, price and date for
			// display
			nameArr = new String[cursor.getCount()];
			priceArr = new String[cursor.getCount()];
			purchDateArr = new String[cursor.getCount()];

			// counter used in arrays
			int i = 0;
			// get each record
			do
			{

				Log.i("ThingseActivity",
						"cursor contents 0 " + cursor.getString(0));
				Log.i("ThingseActivity",
						"cursor contents 1 " + cursor.getString(1));
				Log.i("ThingseActivity",
						"cursor contents 2 " + cursor.getString(2));
				Log.i("ThingseActivity",
						"cursor contents 3 " + cursor.getString(3));
				Log.i("ThingseActivity",
						"cursor contents 4 " + cursor.getString(4));
				Log.i("ThingseActivity",
						"cursor contents 5 " + cursor.getString(5));
				Log.i("ThingseActivity",
						"cursor contents 6 " + cursor.getString(6));

				// create a thing rec
				Thing thing = new Thing();

				// id
				thing.setId(cursor.getString(0));

				// name
				thing.setName(cursor.getString(1));

				nameArr[i] = cursor.getString(1);

				// check for gift and set
				if (cursor.getString(3).equalsIgnoreCase("g"))
				{
					thing.setPrice(" Gift :) ");

					priceArr[i] = " Gift :) ";

				} else
				{
					// format price
					thing.setPrice(cursor.getString(2));
					// priceArr[i] = curSymbol+cursor.getString(2);
					SharedPreferences sharedPrefs = PreferenceManager
							.getDefaultSharedPreferences(this);
					Log.i("ThingseActivity", "Currency chosen is = "
							+ sharedPrefs.getString("currencyPref", "Rs."));
					priceArr[i] = sharedPrefs.getString("currencyPref", "Rs.")
							+ cursor.getString(2);
				}

				// purchase date
				thing.setPurchaseDate(cursor.getString(4));
				purchDateArr[i] = cursor.getString(4);

				// description
				thing.setDescription(cursor.getString(5));

				// pic location
				thing.setPicLocation(cursor.getString(6));

				// image
				thing.setPic(getThingImage(cursor.getString(6)));

				// add rec to list
				thingsArList.add(thing);

				i++;
			} while (cursor.moveToNext());
		}

		// add display arrays to list
		dispThingsArList.add(nameArr);
		dispThingsArList.add(priceArr);
		dispThingsArList.add(purchDateArr);

		// close when done
		adapter.close();

		return dispThingsArList;
	}

	// masterThings array list holding all things
	ArrayList<Thing> thingsArList = null;

}
