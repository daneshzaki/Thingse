package com.daneshzaki.thingse;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AddSomething extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
		this.getWindow().setNavigationBarColor(Color.parseColor("#D0D0D0"));

		setContentView(R.layout.activity_add_something);

        ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP|ActionBar.DISPLAY_SHOW_TITLE);
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33B5E5")));
        actionBar.setTitle("Add something new");
        actionBar.setHomeButtonEnabled(true);


        //set currency selected by user in price label 
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		((TextView)findViewById( R.id.priceField)).setHint("Price (" + sharedPrefs.getString("currencyPref", "Rs.") + ")");
        
        //create the date dialog
        newFragment = new DatePickerFragment();
        
        //if the activity was destroyed before completion restore input values
        
        if(savedInstanceState != null && !savedInstanceState.isEmpty())
        {
	    	//thing name
	    	((EditText)findViewById( R.id.thingField)).setText(savedInstanceState.getString("name"));
	
	    	//price
	    	((EditText)findViewById( R.id.priceField)).setText(savedInstanceState.getString("price"));
	    	
	    	//gift check
	    	if(savedInstanceState.getString("price").trim().startsWith("g"))
	    	{	
	    		((CheckBox)findViewById( R.id.giftCheck)).setChecked(true);	
	    	}
	    	else
	    	{
	    		//not a gift set the price
	    		String priceNoCur = savedInstanceState.getString("price").replaceAll("Rs.","");
	    		
	    		((EditText)findViewById( R.id.priceField)).setText(priceNoCur);	
	    	}
	    	
	    	//purchased date
	    	((Button)findViewById( R.id.datePurchField)).setText(savedInstanceState.getString("purchaseDate"));    	
	    	
	    	//description
	    	((EditText)findViewById( R.id.descField)).setText(savedInstanceState.getString("description"));
	    	
        }
    }

    //save the thing to db
    public void save(View v)
    {
    	    	
    	String[] inputs = getInputs(false);
    	
    	//check if mandatory fields are filled
    	if(inputs != null && inputs[0].length()>0)
    	{
    		//gift check
    		if((inputs[2].equalsIgnoreCase("p") && inputs[1].length()>0) || (inputs[2].equalsIgnoreCase("g")))
    		{
		    	//save details to db
		    	saveInDB(inputs);
		
		    	Toast.makeText(getBaseContext(), thing + " added to your list of things!", Toast.LENGTH_SHORT).show();
		    	
		    	//sending back to the main activity
		    	Intent intent = new Intent(this, ThingseActivity.class);
		        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        startActivity(intent);
    		}
    	}

    }
    
    //call camera to take a pic 
    public void callCamera(View v)
    {
    	Log.i("AddSomething", "Camera button clicked");
    	
    	// create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        
        // create a file to save the image
        fileUri = getOutputMediaFileUri();
        
        // set the image file name
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); 
        
        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    	
    }

    //choose pic from gallery
    public void uploadPic(View v)
    {
    	Log.i("AddSomething", "Upload button clicked");

    	//call gallery to display images
    	//Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    	Intent intent = new Intent(Intent.ACTION_PICK);
    	intent.addCategory(Intent.CATEGORY_DEFAULT);
    	intent.setDataAndType(
    			Uri.fromFile(new File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),"/")),"*/*");
    	
    	startActivityForResult(intent, CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE);
    }
    
    //call date picker dialog to display date
    public void showDatePickerDialog(View v) 
    {
        newFragment.setDatePurchButton((Button) findViewById(R.id.datePurchField));
        newFragment.show(getFragmentManager(), "datePicker");
    }

	//menu items for save
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{

		menu.add("Save").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		return true;

	}
	//handle save
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem)
	{
		Log.i("ViewSomething", "MenuItem=" + menuItem);
		Log.i("ViewSomething", "MenuItem title=" + menuItem.getTitle());

		// save
		if (menuItem.getTitle() != null && menuItem.getTitle().equals("Save"))
		{
			save(new View(this));
		}

		//go back home
		else
		{
			startActivity(new Intent(AddSomething.this, ThingseActivity.class));
		}


		return true;
	}


    //store values to ensure that they are not lost during image selection
    protected void onSaveInstanceState(Bundle bundle)
    {

    	//receive values from the input fields
    	String[] inputs = getInputs(true);
    	
    	//thing name
    	bundle.putString("name", inputs[0]);

    	//thing price
    	bundle.putString("price", inputs[1]);
    	
    	//gift
    	bundle.putString("gift", inputs[2]);
    	
    	//thing date
    	bundle.putString("purchaseDate", inputs[3]);
    	
    	//thing description
    	bundle.putString("description", inputs[4]);
    }
    
    //handle return from camera or gallery (for choose pic)    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {    	    	
    	//if returning from camera
    	if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) 
        {
            if (resultCode == RESULT_OK) 
            {
            	picLocation = fileUri.toString().replaceFirst("file://", "");
            	            	
            	Log.i("AddSomething", "*** picLocation = " + picLocation);
            	
            	// Image captured and saved to fileUri specified in the Intent
            
            	Toast.makeText(this, "Image saved to:\n" + picLocation, Toast.LENGTH_SHORT).show();

				//Jul15
				//((TextView)findViewById(R.id.picLocation)).setText("Image captured!");

				//scale and set pic
				if(picLocation != null && picLocation.trim().length()>0)
				{
					BitmapFactory.Options options = new BitmapFactory.Options();

					//setting to 1/16th of the original size
					options.inSampleSize = 16;

					Bitmap bmp = BitmapFactory.decodeFile(picLocation, options);

					Drawable d = new BitmapDrawable(getResources(), bmp);

					((ImageView)findViewById(R.id.picSelected)).setImageDrawable(d);
				}
				else
				{
					//set a one pixel image if there is no associated pic with this thing
					((ImageView)findViewById(R.id.picSelected)).setImageResource(R.drawable.onepixel);
				}

			}
            else if (resultCode == RESULT_CANCELED) 
            {
                // User cancelled the image capture
            	Log.i("AddSomething", "user cancelled camera use");

            } 
            else 
            {            	
                // Image capture failed, advise user
            	Log.i("AddSomething", "camera capture failed!!!!!!!!!!");
            	return;
            }
        }
    	//if returning from gallery (choosing pic)
    	else if (requestCode == CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE)
    	{    		
            if (resultCode == RESULT_OK) 
            {            	
            	//parse and retrieve the selected image's location
            	picLocation = getFilePathFromContentUri(Uri.parse(data.getDataString()),this.getContentResolver());

        		Log.i("AddSomething", "selected image location is " + picLocation);
        		
            	// Image chosen path            
            	Toast.makeText(this, "Image selected is :\n" + picLocation, Toast.LENGTH_SHORT).show();
            	

				//Jul21
				//((TextView)findViewById(R.id.picLocation)).setText("Image selected!");
				//scale and set pic
				if(picLocation != null && picLocation.trim().length()>0)
				{
					BitmapFactory.Options options = new BitmapFactory.Options();

					//setting to 1/16th of the original size
					options.inSampleSize = 16;

					Bitmap bmp = BitmapFactory.decodeFile(picLocation, options);

					Drawable d = new BitmapDrawable(getResources(), bmp);

					((ImageView)findViewById(R.id.picSelected)).setImageDrawable(d);
				}
				else
				{
					//set a one pixel image if there is no associated pic with this thing
					((ImageView)findViewById(R.id.picSelected)).setImageResource(R.drawable.onepixel);
				}


			}
            else if (resultCode == RESULT_CANCELED) 
            {
                // User cancelled the image capture
            	Log.i("AddSomething", "image selection cancelled" );            	
            } 
            else 
            {
                // Image capture failed, advise user
            	Log.i("AddSomething", "image selection failed!!!!!!!!!!");
            	return;
            	
            }
    		
    	}
    }
    
    //get the image location from the media content provider (gallery)
    private String getFilePathFromContentUri(Uri selectedImageUri, ContentResolver contentResolver) 
    {
        String filePath;
        
        String[] filePathColumn = {MediaColumns.DATA};

        //open a cursor to query gallery app
        Cursor cursor = contentResolver.query(selectedImageUri, filePathColumn, null, null, null);
        
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        
        //get the file path corresponding to the selected image
        filePath = cursor.getString(columnIndex);
        
        cursor.close();
        
        return filePath;
    }

    //read inputs & validate
    private String[] getInputs(boolean saveState)
    {
    	//store inputs in an array 
    	String[] inputs = new String[10];
    	
    	//read inputs from the controls and add to array 
    	
    	//thing name
    	final EditText thingField = (EditText)findViewById( R.id.thingField);

    	thing = thingField.getText().toString();
    	
    	//check thing name is mandatory if saving to DB not saving state 
    	if(thing.trim().length()<=0 && !saveState)
    	{
    		thingField.setHighlightColor(android.graphics.Color.RED);
			thingField.setError("Thing name is mandatory");
			thingField.requestFocus();

			return null;

			//Jul-15
			/*new AlertDialog.Builder(AddSomething.this)
			 .setTitle("Thing name is mandatory ")
			 .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
			     public void onClick(DialogInterface dialog, int whichButton) {
			
			    	 thingField.requestFocus(); 
			    	 Log.i("AddSomething", "ok clicked");
			    	 return;
			     	
			     }
			 }).create().show();*/
		
    		
    	}
    	
    	inputs[0] = thing;

    	//price
    	final EditText priceField = (EditText)findViewById( R.id.priceField);
    	
    	String price = priceField.getText().toString();
    	
    	inputs[1] = price ;

    	//check if its a gift?
    	CheckBox giftCheck = (CheckBox)findViewById( R.id.giftCheck);
    	
    	String gift = "p";
    	
    	if(giftCheck.isChecked())
    	{
    		gift="g";
			//Jul 12 2015 fix set price =0 for gift
			inputs[1] = "0";
    	}
    	
    	inputs[2] = gift;

    	//price or gift check is mandatory 
    	if(price.trim().length() <=0 && gift.equals("p") && !saveState)
    	{
    		priceField.setHighlightColor(android.graphics.Color.RED);
			priceField.setError("Price is mandatory if thing is not gift ");
			priceField.requestFocus();

			return null;

			/* Jul15 -new AlertDialog.Builder(AddSomething.this)
			 .setTitle("Price is mandatory if thing is not gift ")
			 .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
			     public void onClick(DialogInterface dialog, int whichButton) {
			
					Log.i("AddSomething", "ok clicked");
					priceField.requestFocus();
					dialog.dismiss();
			     	
			     }
			 }).create().show();*/

    	}
    	
    	//purchased date
    	inputs[3] = newFragment.getSelectedDate();
    	
    	//description
    	inputs[4] = ((EditText)findViewById( R.id.descField)).getText().toString();
    	
    	//get the picture location and store it in db
    	inputs[5]= picLocation;    	
    	    	
    	//send array list as array    
    	return inputs;
    }
            
    
    // Create a file Uri for saving image captured 
    private static Uri getOutputMediaFileUri()
    {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Thingse");
        
        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists())
        {
            if (! mediaStorageDir.mkdirs())
            {
                Log.d("AddSomething", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
         
        Log.i("AddSomething", "getOutputMediaFileUri file created"+mediaFile.getAbsolutePath());
        
        return Uri.fromFile(mediaFile);
    }

    
    //store inputs into db
    private void saveInDB(String[] inputs)
    {
    	Log.i("AddSomething", "Saving details in database...");
    	
    	//use SQL adapter to store to db 
    	SQLAdapter adapter = new SQLAdapter(this);
    	
    	//open the db through the adapter
    	adapter.open();
    	
    	//insert the details
    	long id = adapter.insertThing(inputs[0], inputs[1], inputs[2], inputs[3], inputs[4], inputs[5]);
    	    	    
    	Log.i("AddSomething", "Record stored with id = "+id);
    	
    	//close the db
    	adapter.close();
    }
    
    //picture location
    private String picLocation = "";
    
    //thing name
    private String thing ="";    

    //uri for picture location
    private Uri fileUri;
    
    //request code for choosing pic from gallery 
    private static final int CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE = 200;
    
    //request code for calling camera
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    
    //for date picker
    private DatePickerFragment newFragment = null;
    
}
