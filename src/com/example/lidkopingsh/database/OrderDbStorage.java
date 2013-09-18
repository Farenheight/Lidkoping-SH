package com.example.lidkopingsh.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.model.Order;

public class OrderDbStorage {
	private SQLiteDatabase db;
	
	public OrderDbStorage(Context context){
		OrderDbHelper dbHelper = new OrderDbHelper(context);
		db = dbHelper.getWritableDatabase();
	}
	public void insert(Order order){
		
	}
}
