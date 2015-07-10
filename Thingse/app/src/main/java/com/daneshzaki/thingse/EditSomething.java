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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.TextView;
import android.widget.Toast;

//This class corresponds to the Edit screen that is called when a "Thing" is edited from the "View Thing" screen
public class EditSomething extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);

        setContentView(R.layout.activity_add_something);

        //set currency selected by user in price label 
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		curSymbol = sharedPrefs.getString("currencyPref", "Rs.");
        ((TextView)findViewById( R.id.priceField)).setText("Price ("+curSymbol+")");
        
        //set values of the selected thing in the input fields
        bundle = this.getIntent().getBundleExtra("thing");

        //create the date picker dialog
        newFragment = new DatePickerFragment();
        
        //load the values of the selected thing on the screen
        loadValues();
        
    }

    //save the thing to db
    public void save(View v)
    {
    	
    	String[] inputs = getInputs(false);

    	//check if mandatory fields are filled
    	if(inputs[0].length()>0)
    	{
    		//gift check
    		if((inputs[2].equalsIgnoreCase("p") && inputs[1].length()>0) || (inputs[2].equalsIgnoreCase("g")))
    		{
    		
	        	//save details to db
	        	saveInDB(inputs);
	
	        	Toast.makeText(getBaseContext(), thing + " updated", Toast.LENGTH_SHORT).show();
	        	    	
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
    	Log.i("EditSomething", "Camera button clicked");
    	
    	// create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // create a file to save the image
        fileUri = getOutputMediaFileUri();
        
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name      

        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    	
    }

    //choose pic from gallery
    public void uploadPic(View v)
    {
    	Log.i("EditSomething", "Upload button clicked");

    	//call gallery to display images
    	//Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    	Intent intent = new Intent(Intent.ACTION_PICK);
    	intent.setDataAndType(
    			Uri.fromFile(new File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),"/")),"*/*");
    	
    	startActivityForResult(intent, CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    //call date picker dialog to display date
    public void showDatePickerDialog(View v) 
    {
        newFragment = new DatePickerFragment();
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

		//sending back to the prev activity
		else
		{
			finish();
		}


		return true;
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {    	
    	//return from camera
    	if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) 
        {
            if (resultCode == RESULT_OK) 
            {
            	picLocation = fileUri.toString().replaceFirst("file://", "");

            	Log.i("AddSomething","picLocation = "+picLocation);
            	
            	// Image captured and saved to fileUri specified in the Intent            
            	Toast.makeText(this, "Image saved to:\n" + picLocation, Toast.LENGTH_LONG).show();
            	
            	((TextView)findViewById(R.id.picLocation)).setText("Image captured!");
            	
            } 
            else if (resultCode == RESULT_CANCELED) 
            {
                // User cancelled the image capture
            	Log.i("EditSomething", "user cancelled camera use");
            } 
            else 
            {
                // Image capture failed, advise user
            	Log.i("EditSomething", "camera failed!!!");
            }
        }
    	//return from gallery app
    	else if (requestCode == CHOOSE_IMAGE_ACTIVITY_REQUEST_CODE)
    	{    		
            if (resultCode == RESULT_OK) 
            {
            	//get the location of the selected image
            	picLocation = getFilePathFromContentUri(Uri.parse(data.getDataString()),this.getContentResolver());
            	
            	Log.i("EditSomething", "onActivityResult image selection location is "+picLocation);

            	// Image chosen path            
            	Toast.makeText(this, "Image selected is :\n" + picLocation, Toast.LENGTH_LONG).show();            	
            	
            	((TextView)findViewById(R.id.picLocation)).setText( "Image selected!");
            	
            } 
            else if (resultCode == RESULT_CANCELED) 
            {
                // User cancelled the image capture
            	Log.i("EditSomething", "user cancelled image selection" );

            } 
            else 
            {
                // Image capture failed, advise user
            	Log.i("EditSomething", "image capture failed!!!" );            	
            }
    		
    	}
    
    }
    
    //parse and get the image location
    private String getFilePathFromContentUri(Uri selectedImageUri, ContentResolver contentResolver) 
    {
        String filePath;
    
        String[] filePathColumn = {MediaColumns.DATA};

        //open a cursor from the content provider
        Cursor cursor = contentResolver.query(selectedImageUri, filePathColumn, null, null, null);
        
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        
        //retrieve file path for selected image
        filePath = cursor.getString(columnIndex);
        
        cursor.close();
        
        return filePath;
    }

    

    //fill fields on screen with selected thing's values
    private void loadValues()
    {    
    	//get all the controls and set values    	
    	((EditText)findViewById( R.id.thingField)).setText(bundle.getString("name"));
    	
        ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33B5E5")));
        actionBar.setTitle( bundle.getString("name"));
        actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);

    	//gift check
    	if(bundle.getString("price").trim().startsWith("Gift :"))
    	{
    		((TextView)findViewById( R.id.datePurchField)).setText("Received on ");
    		((CheckBox)findViewById( R.id.giftCheck)).setChecked(true);	
    	}
    	else
    	{
    		//not a gift set the price
    		String priceNoCur = bundle.getString("price").replaceAll(curSymbol,"");
    		
    		((EditText)findViewById( R.id.priceField)).setText(priceNoCur);	
    	}
    	
    	//purchase date
    	((Button)findViewById( R.id.datePurchField)).setText(bundle.getString("purchaseDate"));
    	
    	//description can be null
    	if(bundle.getString("description")!= null)
    	{
    		((EditText)findViewById( R.id.descField)).setText(bundle.getString("description"));	
    	}
    	else
    	{
    		((EditText)findViewById( R.id.descField)).setText(" ");
    	}
    	
    	//pic location can be null
    	if(bundle.getString("picLocation")!= null)
    	{
    		((TextView)findViewById(R.id.picLabel)).setText( "Replace Picture?");
    	}
    	else
    	{
    		((TextView)findViewById(R.id.picLabel)).setText( "Set Picture");
    	}
    	
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
    	
    	//thing name is mandatory - check during save to db not for screen state saves
    	//check thing name is mandatory if saving to DB not saving state 
    	if(thing.trim().length()<=0 && !saveState)
    	{
    		thingField.setHighlightColor(android.graphics.Color.RED);
    		
			new AlertDialog.Builder(EditSomething.this)                
			 .setTitle("Thing name is mandatory ")
			 .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
			     public void onClick(DialogInterface dialog, int whichButton) {
			
			    	 thingField.requestFocus(); 
			    	 Log.i("EditSomething", "ok clicked");
			    	 dialog.dismiss();
			     	
			     }
			 }).create().show();
		
    		
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
    	}
    	
    	inputs[2] = gift;

    	//price or gift check is mandatory 
    	if(price.trim().length()<=0 && gift.equals("p") && !saveState)
    	{
    		priceField.setHighlightColor(android.graphics.Color.RED);

			new AlertDialog.Builder(EditSomething.this)                
			 .setTitle("Price is mandatory if thing is not gift ")
			 .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
			     public void onClick(DialogInterface dialog, int whichButton) {
			
					Log.i("AddSomething", "ok clicked");
					priceField.requestFocus();
					dialog.dismiss();
			     	
			     }
			 }).create().show();
    		
    	}
    	
    	//purchased date
    	inputs[3] = ((Button)findViewById( R.id.datePurchField)).getText().toString();

    	
    	//description
    	inputs[4] = ((EditText)findViewById( R.id.descField)).getText().toString();
    	
    	//get the picture location and store it in db
    	
    	//if pic location is empty retain old pic
    	if(picLocation.trim().length() == 0)		
		{
			inputs[5]= bundle.getString("picLocation");
		}
    	else
    	{
    		inputs[5]= picLocation;	
    	}    	    	
    	    	
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
                Log.d("EditSomething", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
         
        Log.i("EditSomething", "getOutputMediaFileUri file created");
        
        return Uri.fromFile(mediaFile);
    }

    
    //store inputs into db
    private void saveInDB(String[] inputs)
    {
    	Log.i("EditSomething", "Saving details in database... ");
    	
    	//use SQL adapter to access db 
    	SQLAdapter adapter = new SQLAdapter(this);
    	
    	//open the db through the adapter
    	adapter.open();
    	
    	//update the details
    	boolean status = adapter.updateThing(Long.valueOf(bundle.getString("id")).longValue(), inputs[0], inputs[1], inputs[2], inputs[3], inputs[4], inputs[5]);
    	 
    	Log.i("EditSomething", "Record update status = "+status);
    	
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
    
    //bundle for receiving selected values from previous screen
    private Bundle bundle = null;
    
    //for date picker
    private DatePickerFragment newFragment = null;
    
    //currency
    private String curSymbol ="";

    
}
