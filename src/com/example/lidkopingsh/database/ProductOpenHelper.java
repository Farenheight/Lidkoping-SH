package com.example.lidkopingsh.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProductOpenHelper extends SQLiteOpenHelper{
	private static final int DATABASE_VERSION = 1;
	private static final String PRODUCT_TABLE_NAME = "product";
	private static final String PRODUCT_TABLE_CREATE = 
	@Override
	public void onCreate(SQLiteDatabase db) {
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
}
