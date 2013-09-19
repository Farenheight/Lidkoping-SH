package com.example.lidkopingsh.database;

import java.util.ArrayList;
import java.util.Collection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lidkopingsh.database.DataContract.CustomerTable;
import com.example.lidkopingsh.database.DataContract.OrderTable;
import com.example.lidkopingsh.model.Customer;
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
		insert(order.getCustomer());
		db.insert(OrderTable.TABLE_NAME, null, values);
	}
	private void insert(Customer customer){
		ContentValues values = new ContentValues();
		values.put(CustomerTable.COLUMN_NAME_CUSTOMER_ID, customer.getId());
		values.put(CustomerTable.COLUMN_NAME_ADDRESS, customer.getAddress());
		values.put(CustomerTable.COLUMN_NAME_POSTAL_ADDRESS, customer.getPostAddress());
		values.put(CustomerTable.COLUMN_NAME_EMAIL, customer.getEMail());
		values.put(CustomerTable.COLUMN_NAME_NAME, customer.getName());
		values.put(CustomerTable.COLUMN_NAME_TITLE, customer.getTitle());
		db.insert(CustomerTable.TABLE_NAME, null, values);
		
	}

	public Collection<Order> query(String sqlSelection,
			String[] sqlSelectionArgs, String sqlOrderBy) {
		String sqlQuery = "SELECT * FROM " + OrderTable.TABLE_NAME + " o "
				+ " JOIN " + CustomerTable.TABLE_NAME + " c ON c." 
				+ CustomerTable.COLUMN_NAME_CUSTOMER_ID + " = o." + OrderTable.COLUMN_NAME_CUSTOMER_ID;
		if(sqlSelection != null){
			sqlQuery += " WHERE " + sqlSelection;
		}
		if(sqlOrderBy != null){
			sqlQuery += " ORDER BY " + sqlOrderBy;
		}
		Cursor c = db.rawQuery(sqlQuery, sqlSelectionArgs);
		//Cursor c = db.rawQuery(OrderTable.TABLE_NAME, ORDER_PROJECTION,
			//	sqlSelection, sqlSelectionArgs, null, null, sqlOrderBy);
		Collection<Order> orders = new ArrayList<Order>();
		while (c.moveToNext()) {
			int orderID = getIntColumn(c, OrderTable._ID);
			String orderNumber = getStringColumn(c, OrderTable.COLUMN_NAME_ORDER_NUMBER);
			int orderDate = getIntColumn(c, OrderTable.COLUMN_NAME_ORDER_DATE);
			String cemetery = getStringColumn(c, OrderTable.COLUMN_NAME_CEMETERY);
			int timeCreated = getIntColumn(c, OrderTable.COLUMN_NAME_TIME_CREATED);
			int timeLastUpdate = getIntColumn(c, OrderTable.COLUMN_NAME_TIME_LAST_UPDATE);
			int customerID = getIntColumn(c, OrderTable.COLUMN_NAME_CUSTOMER_ID);
			String address = getStringColumn(c, CustomerTable.COLUMN_NAME_ADDRESS);
			String postalAddress = getStringColumn(c, CustomerTable.COLUMN_NAME_POSTAL_ADDRESS);
			String eMail = getStringColumn(c, CustomerTable.COLUMN_NAME_EMAIL);
			String title = getStringColumn(c, CustomerTable.COLUMN_NAME_TITLE);
			String name = getStringColumn(c, CustomerTable.COLUMN_NAME_NAME);
			orders.add(new Order(orderID, orderNumber, timeCreated, timeLastUpdate, cemetery, orderDate, 
					new Customer(title, name, address, postalAddress, eMail, customerID)));
			
		}
		
		return orders;

	}
	
	private String getStringColumn(Cursor c, String columnName) {
		return c.getString(c.getColumnIndexOrThrow(columnName));
	}
	
	private int getIntColumn(Cursor c, String columnName) {
		return c.getInt(c.getColumnIndexOrThrow(columnName));
	}
}
