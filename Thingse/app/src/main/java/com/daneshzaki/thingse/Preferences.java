package com.daneshzaki.thingse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

//This class is used to display and store the preferences 
public class Preferences extends PreferenceActivity
{
	// this method loads the preferences from the XML file
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33B5E5")));
		actionBar.setTitle("Settings");
		actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
		actionBar.setHomeButtonEnabled(true);

		
		//instantiate db adapter and open db
		adapter = new SQLAdapter(this);
		adapter.open();
		
		// handle preference actions
		
		// about dialog
		Preference aboutPref = (Preference) findPreference("aboutPref");
		aboutPref.setOnPreferenceClickListener(new OnPreferenceClickListener()
		{

			public boolean onPreferenceClick(Preference preference)
			{
				Log.i("Preferences", "about clicked");

				// display about screen
				startActivity(new Intent(Preferences.this, AboutThingse.class));
				return true;
			}
		}

		);

		// export contents to file
		Preference exportPref = (Preference) findPreference("export");
		exportPref.setOnPreferenceClickListener(new OnPreferenceClickListener()
		{

			public boolean onPreferenceClick(Preference preference)
			{
				Log.i("Preferences", "export clicked");
				
				//read db table contents and write to a stringBuffer
				Cursor cursor = adapter.getAllThings();

				//records to be written to file
				StringBuffer records = new StringBuffer();
				
				// navigate through cursor to retrieve all records starting from the first
				if (cursor != null && cursor.moveToFirst())
				{
					
					// get each record
					do
					{

						Log.i("Preferences", "cursor contents 0 " + cursor.getString(0));
						Log.i("Preferences", "cursor contents 1 " + cursor.getString(1));
						Log.i("Preferences", "cursor contents 2 " + cursor.getString(2));
						Log.i("Preferences", "cursor contents 3 " + cursor.getString(3));
						Log.i("Preferences", "cursor contents 4 " + cursor.getString(4));
						Log.i("Preferences", "cursor contents 5 " + cursor.getString(5));
						Log.i("Preferences", "cursor contents 6 " + cursor.getString(6));

						// create a thing rec
						StringBuffer record = new StringBuffer();
						
						record.append(cursor.getString(0));
						record.append(",");
						record.append(cursor.getString(1));
						record.append(",");
						record.append(cursor.getString(2));
						record.append(",");
						record.append(cursor.getString(3));
						record.append(",");
						record.append(cursor.getString(4));
						record.append(",");
						record.append(cursor.getString(5));
						record.append(",");
						record.append(cursor.getString(6));
						record.append("\n");
						
						records.append(record.toString());
						
					} while (cursor.moveToNext());
				}

				writeContentToFile(records.toString(),"thingse.csv");
				
				//display toast with status  
		    	Toast.makeText(getBaseContext(), "Contents exported successfully", Toast.LENGTH_SHORT).show();				
				return true;
			}
		}

		);

		// import contents from file
		Preference importPref = (Preference) findPreference("import");
		importPref.setOnPreferenceClickListener(new OnPreferenceClickListener()
		{

			public boolean onPreferenceClick(Preference preference)
			{
				Log.i("Preferences", "import clicked");
				
				String[] importedContents = readContentFromFile("thingse.csv");
				
				//record counter
				int i = importedContents.length -1;
				
				Log.i("Preferences", "imported contents = \n");
				
				while(i>=0)
				{
					Log.i("Preferences", "record = "+importedContents[i]);
					
					//get fields from rec
					StringTokenizer tokenizer = new StringTokenizer(importedContents[i], ",");
					String[] fields = new String[7];
					
					//field counter
					int j=0;
					while(tokenizer.hasMoreTokens())
					{
						fields[j] = tokenizer.nextToken();
						Log.i("Preferences", "field "+j+" = "+fields[j]);
						j++;
					}
					
					//TODO:check for gift
					if(fields[2] != null && fields[2].trim().equalsIgnoreCase("g"))
					{
						//July 3 2015 fix for pic location
						/*fields[6]=fields[5];
						fields[5]=fields[4];
						fields[4]=fields[3];						
						fields[3]=fields[2];
						fields[2] = "";*/

						fields[6]=fields[5];
						fields[5]=fields[4];
						fields[4]=fields[3];
						fields[3]=fields[2];
						fields[2] = "";

					}
					
					adapter.insertThing(fields[1], fields[2], fields[3], fields[4], fields[5], fields[6]);
					//adapter.insertThing(name, price, gift, purchaseDate, description, picLocation);

					//write importedContents to the db table
					Log.i("Preferences", "writing rec to db - "+fields[1] +" "+ fields[2]+" "+ fields[3]+" "+ fields[4]+" "+ fields[5]+" "+ fields[6]);
					
					
					i--;
				}

				//display toast with status		
		    	Toast.makeText(getBaseContext(), "Contents imported successfully", Toast.LENGTH_SHORT).show();

				return true;
			}
		}

		);

	}

	// show previous activity
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem)
	{
		// sending back to the prev activity
    	Intent intent = new Intent(this, ThingseActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

		//finish();
		return true;
	}

	//handle back button
	@Override
	public void onBackPressed()
	{
		Log.i("Preferences","onBackPressed");
		
    	//sending back to the main activity
    	Intent intent = new Intent(this, ThingseActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
	}

	
	//cleanup db connections
	protected void onDestroy()
	{
		super.onDestroy();

		//close the db
    	adapter.close();

	}
	

	
	/*
	 * This method writes contents to a file
	 * */
	private void writeContentToFile(String content, String fileName)
	{
		// create a file if it does not exist
		File exportFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName);
		
		// check if file exists
		if (!exportFile.exists())
		{
			try
			{
				exportFile.createNewFile();
				Log.i("Preferences", "file created with name - " + exportFile.getAbsolutePath());
			} catch (IOException e)
			{
				Log.e("Preferences", "***unable to create file  " + e.getMessage(), e);
				e.printStackTrace();
			}
		}

		Log.i("Preferences", "file exists with name - " + exportFile.getAbsolutePath());
		// write to the file
		try
		{
			// BufferedWriter for performance, true to set append to file flag
			BufferedWriter buf = new BufferedWriter(new FileWriter(exportFile, false));

			buf.append(content);
			
			buf.close();
		} catch (IOException e)
		{
			Log.e("Preferences", e.getMessage(), e);
			e.printStackTrace();
		}
	}
	
	/*
	 * This method reads content from a file
	 * 
	 * */
    private String[] readContentFromFile(String fileName)
    {	
    	ArrayList <String> list = new ArrayList <String> ();
    	
    	//read from the file
    	try
    	{
			//BufferedReader reader = new BufferedReader(new FileReader(getDir("",MODE_WORLD_WRITEABLE).getAbsolutePath()+fileName));
			BufferedReader reader = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName));
			
			
			String tmp ="";
			
			while((tmp=reader.readLine())!=null)
			{
				list.add(tmp);
			}
			
			reader.close();
    	}
    	catch (IOException e)
    	{
    		Log.e("Preferences", e.getMessage(), e); 
    		e.printStackTrace();
    	}    	    	

		return Arrays.copyOf(list.toArray(), list.toArray().length, String[].class);	
    }    

    //database adapter
    private SQLAdapter adapter = null;
}