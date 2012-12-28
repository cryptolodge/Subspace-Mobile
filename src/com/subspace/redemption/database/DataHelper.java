package com.subspace.redemption.database;

import java.util.ArrayList;
import java.util.List;

import com.subspace.redemption.R;
import com.subspace.redemption.dataobjects.Zone;

import android.content.*;


import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataHelper {
	   private static final String DATABASE_NAME = "ssmobile.db";
	   private static final int DATABASE_VERSION = 1;
	   
	   private static final String TBL_ZONE = "tbl_Zone";
	   
	   private Context context;
	   private SQLiteDatabase db;
	   
	   public DataHelper(Context context)
	   {
		   this.context = context;
		   OpenHelper openHelper = new OpenHelper(context);
		   db = openHelper.getWritableDatabase();		   
	   }
	   
	   public void ExecuteSQL(String sql)
	   {
		   db.beginTransaction();
		   db.execSQL(sql);
		   db.endTransaction();		   
	   }
	   
	   public void addZone(Zone zone)
	   {    
		    ContentValues values = new ContentValues();
		    values.put("name",zone.Name);
		    values.put("description", zone.Description);
		    values.put("ip", zone.Ip);
		    values.put("port", zone.Port);
		    values.put("iscustom", zone.IsCustom);
		 
		    // Inserting Row
		    db.insert(TBL_ZONE, null, values);
		    db.close(); // Closing database connection	   
	   }
	   
	   public List<Zone> getAllCZones() {
		    List<Zone> zoneList = new ArrayList<Zone>();
		    // Select All Query
		    String selectQuery = "SELECT id,name,description,ip,port,iscustom FROM " + context.getString(R.string.tbl_Zone);
		 
		    Cursor cursor = db.rawQuery(selectQuery, null);
		 
		    // looping through all rows and adding to list
		    if (cursor.moveToFirst()) {
		        do {
		            Zone zone = new Zone();
		            zone.Id = Integer.parseInt(cursor.getString(0));
		            zone.Name = cursor.getString(1);
		            zone.Description = cursor.getString(2);
		            zone.Ip = cursor.getString(3);
		            zone.Port = Integer.parseInt(cursor.getString(4));
		            zone.IsCustom = Boolean.parseBoolean(cursor.getString(5));
		            // Adding contact to list
		            zoneList.add(zone);
		        } while (cursor.moveToNext());
		    }
		 
		    // return contact list
		    return zoneList;
		}
	   
	   public void clearZones() {		  
		    db.delete(TBL_ZONE, "iscustom = ?",
		            new String[] { String.valueOf("true") });
		    db.close();
		}
	   
	   private static class OpenHelper extends SQLiteOpenHelper {
		   	 
		   	   private Context context;
		   
	   	      OpenHelper(Context context) {	   	    	 
	   	         super(context, DATABASE_NAME, null, DATABASE_VERSION);
	   	         this.context = context;
	   	      }
	   	 
	   	      @Override
	   	      public void onCreate(SQLiteDatabase db) {
		   	     Log.d("Database", "Creating Subspace Redemption Database");
		   	    	 
		   	     Log.d("Database", context.getString(R.string.tbl_Zone));
		   	     db.execSQL(context.getString(R.string.tbl_Zone));
	   	      }
	   	 
	   	      @Override
	   	      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	   	  //       Log.w("Example", "Upgrading database, this will drop tables and recreate.");
	   	    //     db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	   	         onCreate(db);
	   	      }
	   }
}
	   

	

