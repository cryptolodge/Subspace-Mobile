package com.subspace.redemption.database;

import com.subspace.redemption.R;


import android.content.*;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataHelper {
	   private static final String DATABASE_NAME = "ssmobile.db";
	   private static final int DATABASE_VERSION = 1;
	   
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
	   
	   private static class OpenHelper extends SQLiteOpenHelper {
		   	 
	   	      OpenHelper(Context context) {
	   	         super(context, DATABASE_NAME, null, DATABASE_VERSION);
	   	      }
	   	 
	   	      @Override
	   	      public void onCreate(SQLiteDatabase db) {
	   	    // Log.d("Database", "Creating Subspace Redemtion Database");
	   	    	  //
	   	    //	  Log.d("Database", getString(R.string.tbl_Settings));
	   	   //       db.execSQL(getString(R.string.tbl_Settings));
	   	      }
	   	 
	   	      @Override
	   	      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	   	  //       Log.w("Example", "Upgrading database, this will drop tables and recreate.");
	   	    //     db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	   	         onCreate(db);
	   	      }
	   }
}
	   

	

