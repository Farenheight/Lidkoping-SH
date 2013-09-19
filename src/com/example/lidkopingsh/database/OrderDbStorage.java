package com.example.lidkopingsh.database;

import java.util.Collection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lidkopingsh.database.DataContract.OrderTable;
import com.example.lidkopingsh.model.Order;

public class OrderDbStorage {
	private SQLiteDatabase db;
	private static final String[] ORDER_PROJECTION = { OrderTable._ID,
			OrderTable.COLUMN_NAME_ORDER_NUMBER,
			OrderTable.COLUMN_NAME_ORDER_DATE,
			OrderTable.COLUMN_NAME_CUSTOMER_ID,
			OrderTable.COLUMN_NAME_CEMETERY,
			OrderTable.COLUMN_NAME_TIME_CREATED,
			OrderTable.COLUMN_NAME_TIME_LAST_UPDATE };

	public OrderDbStorage(Context context) {
		OrderDbHelper dbHelper = new OrderDbHelper(context);
		db = dbHelper.getWritableDatabase();
	}

	public void insert(Order order) {
		ContentValues values = new ContentValues();
		values.put(OrderTable.COLUMN_NAME_ORDER_NUMBER, order.getOrderNumber());
		values.put(OrderTable.COLUMN_NAME_ORDER_DATE, order.getOrderDate());
		values.put(OrderTable.COLUMN_NAME_CUSTOMER_ID, order.getCustomer()
				.getId());
		values.put(OrderTable.COLUMN_NAME_CEMETERY, order.getCementary());
		values.put(OrderTable.COLUMN_NAME_TIME_CREATED, order.getTimeCreated());
		values.put(OrderTable.COLUMN_NAME_TIME_LAST_UPDATE,
				order.getLastTimeUpdate());
		db.insert(OrderTable.TABLE_NAME, null, values);
	}

	public Collection<Order> query(String sqlSelection,
			String[] sqlSelectionArgs, String sqlOrderBy) {
		Cursor c = db.query(OrderTable.TABLE_NAME, ORDER_PROJECTION,
				sqlSelection, sqlSelectionArgs, null, null, sqlOrderBy);
		while (c.moveToNext()) {
			int orderID = getIntColumn(c, OrderTable._ID);
			String orderNumber = getStringColumn(c, OrderTable.COLUMN_NAME_ORDER_NUMBER);
			int orderDate = getIntColumn(c, OrderTable.COLUMN_NAME_ORDER_DATE);
			String cemetery = getStringColumn(c, OrderTable.COLUMN_NAME_CEMETERY);
			int customerID = getIntColumn(c, OrderTable.COLUMN_NAME_CUSTOMER_ID);
			int timeCreated = getIntColumn(c, OrderTable.COLUMN_NAME_TIME_CREATED);
			int timeLastUpdate = getIntColumn(c, OrderTable.COLUMN_NAME_TIME_LAST_UPDATE);
			
		}
		
		return null;

	}
	
	private String getStringColumn(Cursor c, String columnName) {
		return c.getString(c.getColumnIndexOrThrow(columnName));
	}
	
	private int getIntColumn(Cursor c, String columnName) {
		return c.getInt(c.getColumnIndexOrThrow(columnName));
	}
}
