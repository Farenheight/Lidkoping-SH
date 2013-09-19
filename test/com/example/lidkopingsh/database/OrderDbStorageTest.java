package com.example.lidkopingsh.database;

import java.util.Collection;

import org.junit.Test;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.test.InstrumentationTestCase;

import com.example.lidkopingsh.database.DataContract.OrderTable;
import com.example.lidkopingsh.model.Customer;
import com.example.lidkopingsh.model.Order;

public class OrderDbStorageTest extends InstrumentationTestCase{

	@Test
	public void testSelect() {
		SQLiteDatabase db;
		OrderDbHelper dbHelper = new OrderDbHelper(getInstrumentation().getContext());
		db = dbHelper.getWritableDatabase();
		Order order = new Order(1, "" + 123, System.currentTimeMillis(), 1111, "borta", 1002, 
				new Customer("Mr", "namn", "adress", "postAdress", "email", 23));
		/*ContentValues values = new ContentValues();
		values.put(OrderTable.COLUMN_NAME_ORDER_NUMBER, order.getOrderNumber());
		values.put(OrderTable.COLUMN_NAME_ORDER_DATE, order.getOrderDate());
		values.put(OrderTable.COLUMN_NAME_CUSTOMER_ID, order.getCustomer()
				.getId());
		values.put(OrderTable.COLUMN_NAME_CEMETERY, order.getCementary());
		values.put(OrderTable.COLUMN_NAME_TIME_CREATED, order.getTimeCreated());
		values.put(OrderTable.COLUMN_NAME_TIME_LAST_UPDATE,
				order.getLastTimeUpdate());*/
		OrderDbStorage dbStorage = new OrderDbStorage(getInstrumentation().getContext());
		dbStorage.insert(order);
		Collection<Order> orders = dbStorage.query(null, null, null);
		assertTrue(orders.contains(order));
	}

}
