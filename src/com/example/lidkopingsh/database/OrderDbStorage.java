package com.example.lidkopingsh.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lidkopingsh.database.DataContract.CustomerTable;
import com.example.lidkopingsh.database.DataContract.OrderTable;
import com.example.lidkopingsh.database.DataContract.ProductTable;
import com.example.lidkopingsh.database.DataContract.StoneTable;
import com.example.lidkopingsh.database.DataContract.TaskTable;
import com.example.lidkopingsh.database.DataContract.TaskToProductTable;
import com.example.lidkopingsh.model.Customer;
import com.example.lidkopingsh.model.Order;
import com.example.lidkopingsh.model.Product;
import com.example.lidkopingsh.model.Status;
import com.example.lidkopingsh.model.Stone;
import com.example.lidkopingsh.model.Task;

/**
 * Query, insert into and update the Order database content. This class converts
 * data between the {@link Order} model object and database table format.
 * 
 * @author Anton Jansson
 * @author Olliver Mattsson
 * 
 */
public class OrderDbStorage {

	private final SQLiteDatabase db;

	/**
	 * Create a Order database connection for querying, adding or updating Order
	 * database content.
	 * 
	 * @param context
	 *            to use to open or create the database
	 */
	public OrderDbStorage(Context context) {
		OrderDbHelper dbHelper = new OrderDbHelper(context);
		db = dbHelper.getWritableDatabase();
	}

	/**
	 * Insert a new order into the database.
	 * 
	 * @param order
	 *            The order to insert.
	 */
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

		insertCustomer(order.getCustomer());

		db.insert(OrderTable.TABLE_NAME, null, values);
	}

	/**
	 * Insert a new customer into the database. The customer object must contain
	 * its customer id, which is referred to in the Order table.
	 * 
	 * @param customer
	 *            The customer to insert.
	 */
	private void insertCustomer(Customer customer) {
		ContentValues values = new ContentValues();

		values.put(CustomerTable.COLUMN_NAME_CUSTOMER_ID, customer.getId());
		values.put(CustomerTable.COLUMN_NAME_ADDRESS, customer.getAddress());
		values.put(CustomerTable.COLUMN_NAME_POSTAL_ADDRESS,
				customer.getPostAddress());
		values.put(CustomerTable.COLUMN_NAME_EMAIL, customer.getEMail());
		values.put(CustomerTable.COLUMN_NAME_NAME, customer.getName());
		values.put(CustomerTable.COLUMN_NAME_TITLE, customer.getTitle());

		db.insert(CustomerTable.TABLE_NAME, null, values);
	}

	public Collection<Order> query(String sqlSelection,
			String[] sqlSelectionArgs, String sqlOrderBy) {
		String sqlQuery = "SELECT * FROM " + OrderTable.TABLE_NAME + " o "
				+ " JOIN " + CustomerTable.TABLE_NAME + " c ON c."
				+ CustomerTable.COLUMN_NAME_CUSTOMER_ID + " = o."
				+ OrderTable.COLUMN_NAME_CUSTOMER_ID;
		if (sqlSelection != null) {
			sqlQuery += " WHERE " + sqlSelection;
		}
		if (sqlOrderBy != null) {
			sqlQuery += " ORDER BY " + sqlOrderBy;
		}

		Cursor c = db.rawQuery(sqlQuery, sqlSelectionArgs);
		Collection<Order> orders = new ArrayList<Order>();

		while (c.moveToNext()) {
			orders.add(getOrder(c));
		}

		return orders;
	}

	private Customer getCustomer(Cursor c) {
		int customerID = getIntColumn(c, OrderTable.COLUMN_NAME_CUSTOMER_ID);
		String address = getStringColumn(c, CustomerTable.COLUMN_NAME_ADDRESS);
		String postalAddress = getStringColumn(c,
				CustomerTable.COLUMN_NAME_POSTAL_ADDRESS);
		String eMail = getStringColumn(c, CustomerTable.COLUMN_NAME_EMAIL);
		String title = getStringColumn(c, CustomerTable.COLUMN_NAME_TITLE);
		String name = getStringColumn(c, CustomerTable.COLUMN_NAME_NAME);

		return new Customer(title, name, address, postalAddress, eMail,
				customerID);
	}

	private Order getOrder(Cursor c) {
		int orderID = getIntColumn(c, OrderTable._ID);
		long orderDate = getIntColumn(c, OrderTable.COLUMN_NAME_ORDER_DATE);
		String orderNumber = getStringColumn(c,
				OrderTable.COLUMN_NAME_ORDER_NUMBER);
		String cemetery = getStringColumn(c, OrderTable.COLUMN_NAME_CEMETERY);
		int timeCreated = getIntColumn(c, OrderTable.COLUMN_NAME_TIME_CREATED);
		int timeLastUpdate = getIntColumn(c,
				OrderTable.COLUMN_NAME_TIME_LAST_UPDATE);
		
		Order order = new Order(orderID, orderNumber, timeCreated, timeLastUpdate,
				cemetery, orderDate, getCustomer(c));
		order.addProducts(getProducts(orderNumber));
		
		return order;
	}
	
	private Product getProduct(Cursor c) {
		int productId = getIntColumn(c, ProductTable.COLUMN_NAME_PRODUCT_ID);
		String title = getStringColumn(c, ProductTable.COLUMN_NAME_TITLE);
		String orderNumber = getStringColumn(c, ProductTable.COLUMN_NAME_ORDER_NUMBER);
		String description = getStringColumn(c, ProductTable.COLUMN_NAME_DESCRIPTION);
		String materialColor = getStringColumn(c, ProductTable.COLUMN_NAME_MATERIAL_COLOR);
		String frontWork = getStringColumn(c, ProductTable.COLUMN_NAME_FRONT_WORK);
		
		List<Task> tasks = getTasks(c);
		
		boolean isStone = !c.isNull(c.getColumnIndexOrThrow(StoneTable.COLUMN_NAME_STONE_ID));
		if (isStone) {
			return getStone(c, productId, title, orderNumber, description, materialColor, frontWork, tasks);
		} else {
			return new Product(productId, materialColor, description, frontWork, tasks);
		}
	}

	private Collection<Product> getProducts(String orderNumber) {
		String sqlProducts = "SELECT * FROM " + TaskTable.TABLE_NAME + " t"
				+ " JOIN " + TaskToProductTable.TABLE_NAME + " ttp ON t." 
				+ TaskTable.COLUMN_NAME_TASK_ID + " = ttp." + TaskToProductTable.COLUMN_NAME_TASK_ID
				+ " JOIN " + ProductTable.TABLE_NAME + " p ON ttp." 
				+ TaskToProductTable.COLUMN_NAME_PRODUCT_ID	+ " = p." + ProductTable.COLUMN_NAME_PRODUCT_ID
				+ " JOIN " + StoneTable.TABLE_NAME + " s ON p."
				+ ProductTable.COLUMN_NAME_PRODUCT_ID + " = s." + StoneTable.COLUMN_NAME_PRODUCT_ID
				+ " WHERE p." + ProductTable.COLUMN_NAME_ORDER_NUMBER + " = ?"
				+ " ORDER BY p." + ProductTable.COLUMN_NAME_PRODUCT_ID 
				+ ", ttp." + TaskToProductTable.COLUMN_NAME_SORT_ORDER;
		
		Cursor c = db.rawQuery(sqlProducts, new String[] { orderNumber });
		Collection<Product> products = new LinkedList<Product>();
		
		while (c.moveToNext()) {
			products.add(getProduct(c));
		}
		
		return products;
	}
	
	private Stone getStone(Cursor c, int productId, String title,
			String orderNumber, String description, String materialColor,
			String frontWork, List<Task> tasks) {
		String stoneModel = getStringColumn(c,
				StoneTable.COLUMN_NAME_STONE_MODEL);
		String sideBackwork = getStringColumn(c,
				StoneTable.COLUMN_NAME_SIDE_BACK_WORK);
		String textStyle = getStringColumn(c, StoneTable.COLUMN_NAME_TEXTSTYLE);
		String ornament = getStringColumn(c, StoneTable.COLUMN_NAME_ORNAMENT);

		return new Stone(productId, materialColor, description, frontWork,
				tasks, stoneModel, sideBackwork, textStyle, ornament);
	}
	
	private Task getTask(Cursor c) {
		int taskId = getIntColumn(c, TaskTable.COLUMN_NAME_TASK_ID);
		String name = getStringColumn(c, TaskTable.COLUMN_NAME_TASK);
		int status = getIntColumn(c, TaskToProductTable.COLUMN_NAME_TASK_STATUS);
		
		return new Task(taskId, name, Status.values()[status]);
	}
	
	private List<Task> getTasks(Cursor c) {
		List<Task> tasks = new LinkedList<Task>();
		int productId = getIntColumn(c, ProductTable.COLUMN_NAME_PRODUCT_ID);
		
		do {
			if (productId != getIntColumn(c, ProductTable.COLUMN_NAME_PRODUCT_ID)) {
				c.moveToPrevious();
				break;
			}
			tasks.add(getTask(c));
		} while (c.moveToNext());
		
		return tasks;
	}

	private String getStringColumn(Cursor c, String columnName) {
		return c.getString(c.getColumnIndexOrThrow(columnName));
	}

	private int getIntColumn(Cursor c, String columnName) {
		return c.getInt(c.getColumnIndexOrThrow(columnName));
	}
}
