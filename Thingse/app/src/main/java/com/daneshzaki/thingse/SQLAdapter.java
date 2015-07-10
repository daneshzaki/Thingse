package com.daneshzaki.thingse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//This class performs database operations
//Code courtesy: Beginning Android 4 Development book
public class SQLAdapter 
{   
    public SQLAdapter(Context ctx)
    {
        this.context = ctx;
        sqlHelper = new SQLHelper(context);
    }

    //inner class to help db create and upgrade
    private static class SQLHelper extends SQLiteOpenHelper
    {
        SQLHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try 
            {
                db.execSQL(DATABASE_CREATE);
            } 
            catch (SQLException e) 
            {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.i(TAG, "Upgrading database from version " + oldVersion + " to " 
            		+ newVersion + ", which will destroy all old data");
            
            db.execSQL("DROP TABLE IF EXISTS contacts");
            
            onCreate(db);
        }
    }

    //opens the database
    public SQLAdapter open() throws SQLException 
    {
    	Log.i(TAG, "open");
        db = sqlHelper.getWritableDatabase();
        return this;
    }

    //closes the database
    public void close() 
    {
    	Log.i(TAG, "close");
        sqlHelper.close();
    }

    //insert a thing into the database
    public long insertThing(String name, String price, 
    						String gift, String purchaseDate, 
    						String description,String picLocation) 
    {    	
        ContentValues initialValues = new ContentValues();
        
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_PRICE, price);
        initialValues.put(KEY_GIFT, gift);
        initialValues.put(KEY_PURCHASEDATE, purchaseDate);
        initialValues.put(KEY_DESCRIPTION, description);
        initialValues.put(KEY_PICLOCATION, picLocation);
        
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    //deletes a particular thing
    public boolean deleteThing(long rowId) 
    {
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    //retrieves all the things
    public Cursor getAllThings()
    {
    	try
    	{
            return db.query(DATABASE_TABLE, 
            		new String[] {KEY_ROWID, 
            		KEY_NAME,
            		KEY_PRICE, 
            		KEY_GIFT, 
            		KEY_PURCHASEDATE, 
            		KEY_DESCRIPTION, 
            		KEY_PICLOCATION}, 
            		null, null, null, null, KEY_PURCHASEDATE);
    		
    		
    	}
    	catch(Exception e)
    	{
    		Log.i(TAG, "getAllThings exception: \n"+e);
    		return null;
    	}
    }

    //retrieves a particular thing
    public Cursor getThing(long rowId) throws SQLException 
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, 
                		KEY_NAME,
                		KEY_PRICE, 
                		KEY_GIFT, 
                		KEY_PURCHASEDATE, 
                		KEY_DESCRIPTION, 
                		KEY_PICLOCATION},
                		KEY_ROWID + "=" + rowId, null,null, null, null, null);
        
        if (mCursor != null) 
        {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //updates a thing
    public boolean updateThing(long rowId, String name, String price, 
							String gift, String purchaseDate, 
							String description,String picLocation)  
    {
        ContentValues args = new ContentValues();
        
        args.put(KEY_NAME, name);
        args.put(KEY_PRICE, price);
        args.put(KEY_GIFT, gift);
        args.put(KEY_PURCHASEDATE, purchaseDate);
        args.put(KEY_DESCRIPTION, description);
        args.put(KEY_PICLOCATION, picLocation);
        
        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

	/*
	  Table: Thing
	  Columns:

		_id
		Name TEXT
		Price REAL
		Gift TEXT
		PurchaseDate TEXT
		Description TEXT
		PictureLocation TEXT

	 */
	
	//Table columns
	private static final String KEY_ROWID = "_id";
	private static final String KEY_NAME = "name";
	private static final String KEY_PRICE = "price";
	private static final String KEY_GIFT = "gift";
	private static final String KEY_PURCHASEDATE = "purchaseDate";
	private static final String KEY_DESCRIPTION = "description";
	private static final String KEY_PICLOCATION = "picLocation";            
  
	private static final String TAG = "SQLAdapter";

	//Database and table name
	private static final String DATABASE_NAME = "ThingseDB";
	private static final String DATABASE_TABLE = "Thing";
	private static final int DATABASE_VERSION = 1;

	//DDL
	private static final String DATABASE_CREATE =
      "create table thing (_id integer primary key autoincrement, "
      + "name text not null, price real, gift text, purchaseDate datetime, description text, picLocation text);";

  private final Context context;

  private SQLHelper sqlHelper;
  private SQLiteDatabase db;

}

