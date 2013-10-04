package se.chalmers.lidkopingsh.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import se.chalmers.lidkopingsh.database.DataContract.CustomerTable;
import se.chalmers.lidkopingsh.database.DataContract.ImageTable;
import se.chalmers.lidkopingsh.database.DataContract.OrderTable;
import se.chalmers.lidkopingsh.database.DataContract.ProductTable;
import se.chalmers.lidkopingsh.database.DataContract.ProductTypeTable;
import se.chalmers.lidkopingsh.database.DataContract.StationTable;
import se.chalmers.lidkopingsh.database.DataContract.StoneTable;
import se.chalmers.lidkopingsh.database.DataContract.TaskTable;
import se.chalmers.lidkopingsh.model.Customer;
import se.chalmers.lidkopingsh.model.Image;
import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.model.Product;
import se.chalmers.lidkopingsh.model.ProductType;
import se.chalmers.lidkopingsh.model.Station;
import se.chalmers.lidkopingsh.model.Status;
import se.chalmers.lidkopingsh.model.Stone;
import se.chalmers.lidkopingsh.model.Task;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract.DeletedContacts;

/**
 * Query, insert into and update the Order database content. This class converts
 * data between the {@link Order} model object and database table format.
 * When inserting an object it must contain an id since it has to be matched with the other tables in the
 * database.
 * 
 * @author Anton Jansson
 * @author Olliver Mattsson
 * 
 */
class OrderDbStorage {

	private static final String CUSTOMER = "c";
	private static final String IMAGE = "i";
	private static final String ORDER = "o";
	private static final String PRODUCT = "p";
	private static final String PRODUCT_TYPE = "pt";
	private static final String STONE = "s";
	private static final String TASK = "t";
	private static final String TASK_TO_PRODUCT = "ttp";

	private static final String COMMA_SEP = ", ";
	private static final String DOT = ".";
	private static final String EQUALS = " = ";
	private static final String JOIN = " JOIN ";
	private static final String LEFT_JOIN = " LEFT JOIN ";
	private static final String ON = " ON ";
	private static final String ORDER_BY = " ORDER BY ";
	private static final String QUESTION_MARK = "?";
	private static final String SELECT_FROM = "SELECT * FROM ";
	private static final String SPACE = " ";
	private static final String WHERE = " WHERE ";

	private final SQLiteDatabase db;

	private final Collection<Integer> stationIds;
	private final Collection<Integer> productTypeIds;

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
		stationIds = new ArrayList<Integer>();
		productTypeIds = new ArrayList<Integer>();
	}

	/**
	 * Clear all rows in the database.
	 */
	public void clear() {
		db.delete(TaskTable.TABLE_NAME, null, null);
		db.delete(StationTable.TABLE_NAME, null, null);
		db.delete(StoneTable.TABLE_NAME, null, null);
		db.delete(ImageTable.TABLE_NAME, null, null);
		db.delete(ProductTable.TABLE_NAME, null, null);
		db.delete(ProductTypeTable.TABLE_NAME, null, null);
		db.delete(CustomerTable.TABLE_NAME, null, null);
		db.delete(OrderTable.TABLE_NAME, null, null);
	}

	/**
	 * Insert a new order into the database.
	 * 
	 * @param order
	 *            The order to insert.
	 */
	public void insert(Order order) {
		if (order == null) {
			throw new IllegalArgumentException("Order cannot be null");
		}

		db.beginTransaction();
		insertOrder(order);
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	/**
	 * Insert new orders into the database.
	 * 
	 * @param orders A collection of orders to insert.
	 */
	public void insert(Collection<Order> orders) {
		if (orders == null) {
			throw new IllegalArgumentException("Orders cannot be null");
		}

		db.beginTransaction();
		for (Order order : orders) {
			insertOrder(order);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	private void insertOrder(Order order) {
		stationIds.addAll(getStationIds());
		productTypeIds.addAll(getProductTypeIds());

		ContentValues values = new ContentValues();

		values.put(OrderTable.COLUMN_NAME_ORDER_ID, order.getId());
		values.put(OrderTable.COLUMN_NAME_ORDER_NUMBER, order.getOrderNumber());
		values.put(OrderTable.COLUMN_NAME_ORDER_DATE, order.getOrderDate());
		values.put(OrderTable.COLUMN_NAME_CUSTOMER_ID, order.getCustomer()
				.getId());
		values.put(OrderTable.COLUMN_NAME_ID_NAME, order.getIdName());
		values.put(OrderTable.COLUMN_NAME_CEMETERY_BOARD,
				order.getCemeteryBoard());
		values.put(OrderTable.COLUMN_NAME_CEMETERY_BOARD, order.getCemeteryBoard());
		values.put(OrderTable.COLUMN_NAME_CEMETERY, order.getCemetary());
		values.put(OrderTable.COLUMN_NAME_CEMETERY_BLOCK,
				order.getCemetaryBlock());
		values.put(OrderTable.COLUMN_NAME_CEMETERY_NUMBER,
				order.getCemetaryNumber());
		values.put(OrderTable.COLUMN_NAME_TIME_CREATED, order.getTimeCreated());
		values.put(OrderTable.COLUMN_NAME_TIME_LAST_UPDATE,
				order.getLastTimeUpdate());

		insertCustomer(order.getCustomer());
		for (Image i : order.getImages()) {
			insertImage(i, order.getId());
		}

		for (Product p : order.getProducts()) {
			insertProduct(p, order.getId());
		}

		db.insert(OrderTable.TABLE_NAME, null, values);
	}

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

	private void insertImage(Image i, int orderId) {
		ContentValues values = new ContentValues();

		values.put(ImageTable.COLUMN_NAME_IMAGE_ID, i.getId());
		values.put(ImageTable.COLUMN_NAME_ORDER_ID, orderId);
		values.put(ImageTable.COLUMN_NAME_IMAGE, i.getImagePath());

		db.insert(ImageTable.TABLE_NAME, null, values);
	}

	private void insertProduct(Product p, int orderId) {
		ContentValues values = new ContentValues();

		if (!productTypeIds.contains(p.getType().getId())) {
			values.put(ProductTypeTable.COLUMN_NAME_PRODUCT_TYPE_ID, p
					.getType().getId());
			values.put(ProductTypeTable.COLUMN_NAME_TYPE, p.getType().getName());
			
			db.insert(ProductTypeTable.TABLE_NAME, null, values);
			productTypeIds.add(p.getType().getId());
		}

		values = new ContentValues();
		
		values.put(ProductTable.COLUMN_NAME_PRODUCT_ID, p.getId());
		values.put(ProductTable.COLUMN_NAME_ORDER_ID, orderId);
		values.put(ProductTable.COLUMN_NAME_PRODUCT_TYPE_ID, p.getType().getId());
		values.put(ProductTable.COLUMN_NAME_DESCRIPTION, p.getDescription());
		values.put(ProductTable.COLUMN_NAME_MATERIAL_COLOR,
				p.getMaterialColor());
		values.put(ProductTable.COLUMN_NAME_FRONT_WORK, p.getFrontWork());

		db.insert(ProductTable.TABLE_NAME, null, values);

		if (p instanceof Stone) {
			insertStone((Stone) p);
		}

		int i = 0;
		for (Task t : p.getTasks()) {
			insertTask(t, p.getId(), i);
			i++;
		}
	}

	private void insertStone(Stone s) {
		ContentValues values = new ContentValues();

		values.put(StoneTable.COLUMN_NAME_PRODUCT_ID, s.getId());
		values.put(StoneTable.COLUMN_NAME_STONE_MODEL, s.getStoneModel());
		values.put(StoneTable.COLUMN_NAME_SIDE_BACK_WORK, s.getSideBackWork());
		values.put(StoneTable.COLUMN_NAME_TEXTSTYLE, s.getTextStyle());
		values.put(StoneTable.COLUMN_NAME_ORNAMENT, s.getOrnament());

		db.insert(StoneTable.TABLE_NAME, null, values);
	}

	private void insertTask(Task t, int productId, int sortOrder) {
		// Task table
		ContentValues values = new ContentValues();

		if (!stationIds.contains(t.getStation().getId())) {
			values.put(StationTable.COLUMN_NAME_STATION_ID, t.getStation()
					.getId());
			values.put(StationTable.COLUMN_NAME_STATION, t.getStation()
					.getName());

			db.insert(StationTable.TABLE_NAME, null, values);
			stationIds.add(t.getStation().getId());
		}

		// Task to product table
		values = new ContentValues();

		values.put(TaskTable.COLUMN_NAME_PRODUCT_ID, productId);
		values.put(TaskTable.COLUMN_NAME_STATION_ID, t.getStation().getId());
		values.put(TaskTable.COLUMN_NAME_TASK_STATUS, t.getStatus().getId());
		values.put(TaskTable.COLUMN_NAME_SORT_ORDER, sortOrder);

		db.insert(TaskTable.TABLE_NAME, null, values);
	}

	/**
	 * Deletes an order
	 * 
	 * @param order The order to be deleted.
	 */
	public void delete(Order order) {
		db.beginTransaction();
		deleteOrder(order);
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
	/**
	 * Delete an order
	 * 
	 * @param order The order to be deleted
	 */
	public void deleteOrder(Order order){
		for (Product p : order.getProducts()) {
			//checks if there are any products with the given type left if not the type is removed
			Cursor cursor = db.query(ProductTable.TABLE_NAME, null,
					ProductTable.COLUMN_NAME_PRODUCT_TYPE_ID + EQUALS
							+ QUESTION_MARK, new String[] { String
							.valueOf(p.getType().getId()) },
					null, null, null);
			if(!cursor.moveToNext()){
				db.delete(ProductTypeTable.TABLE_NAME,
						ProductTypeTable.COLUMN_NAME_PRODUCT_TYPE_ID + EQUALS
								+ QUESTION_MARK, new String[] { String
								.valueOf(p.getType().getId()) });
				productTypeIds.remove(p.getType().getId());
			}
			db.delete(TaskTable.TABLE_NAME, TaskTable.COLUMN_NAME_PRODUCT_ID
					+ EQUALS + QUESTION_MARK,
					new String[] { String.valueOf(p.getId()) });
			db.delete(StoneTable.TABLE_NAME, StoneTable.COLUMN_NAME_PRODUCT_ID
					+ EQUALS + QUESTION_MARK,
					new String[] { String.valueOf(p.getId()) });

			for (Task t : p.getTasks()) {
				Cursor c = db//checks if there are any tasks with the given station if not the station is removed
						.query(TaskTable.TABLE_NAME, null,
								TaskTable.COLUMN_NAME_STATION_ID + EQUALS
										+ QUESTION_MARK, new String[] { String
										.valueOf(t.getStation().getId()) },
								null, null, null);
				if (!c.moveToNext()) {
					db.delete(StationTable.TABLE_NAME,
							StationTable.COLUMN_NAME_STATION_ID + EQUALS
									+ QUESTION_MARK, new String[] { String
									.valueOf(t.getStation().getId()) });
					stationIds.remove(t.getStation().getId());
				}
			}

		}

		db.delete(ImageTable.TABLE_NAME, ImageTable.COLUMN_NAME_ORDER_ID
				+ EQUALS + QUESTION_MARK,
				new String[] { Integer.toString(order.getId()) });
		db.delete(ProductTable.TABLE_NAME, ProductTable.COLUMN_NAME_ORDER_ID
				+ EQUALS + QUESTION_MARK,
				new String[] { Integer.toString(order.getId()) });
		db.delete(CustomerTable.TABLE_NAME,
				CustomerTable.COLUMN_NAME_CUSTOMER_ID + EQUALS + QUESTION_MARK,
				new String[] { String.valueOf(order.getCustomer().getId()) });
		db.delete(OrderTable.TABLE_NAME, OrderTable.COLUMN_NAME_ORDER_NUMBER
				+ EQUALS + QUESTION_MARK,
				new String[] { order.getOrderNumber() });
		
	}

	/**
	 * Updates an order.
	 * 
	 * @param order The order to be updated.
	 */
	public void update(Order order) {
		delete(order);
		insert(order);
	}
	
	/**
	 * Updates several orders.
	 * 
	 * @param orders The orders to be updated.
	 */
	public void updateOrders(Collection<Order> orders){
		db.beginTransaction();
		
		for(Order o : orders){
			deleteOrder(o);
			insertOrder(o);
		}
		
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	/**
	 * <p>
	 * Select orders from the database.
	 * </p>
	 * <p>
	 * Column names are listed in {@link DataContract}.
	 * </p>
	 * <p>
	 * <strong>Example</strong>
	 * </p>
	 * <code>SELECT * FROM Orders WHERE OrderNumber = '130001' ORDER BY OrderDate</code>
	 * <p>
	 * <b>sqlSelection</b>:
	 * <code>{@link OrderTable#COLUMN_NAME_ORDER_NUMBER} = ?</code><br />
	 * <b>sqlSelectionArgs</b>: <code>new String[] { "130001" }</code><br />
	 * <b>sqlOrderBy</b>:
	 * <code>{@link OrderTable#COLUMN_NAME_ORDER_DATE} ASC</code>
	 * </p>
	 * 
	 * @param sqlSelection
	 *            SQL Where command with arguments replaced by '?'.
	 * @param sqlSelectionArgs
	 *            Arguments for SQL Where command as a String array. All '?' are
	 *            replaced in the array order.
	 * @param sqlOrderBy
	 *            A (comma separated list as a string) that defines the SQL
	 *            Order By.
	 * @return A collection of orders.
	 */
	public Collection<Order> query(String sqlSelection,
			String[] sqlSelectionArgs, String sqlOrderBy) {
		String sqlQuery = SELECT_FROM + OrderTable.TABLE_NAME + SPACE + ORDER
				+ SPACE + JOIN + CustomerTable.TABLE_NAME + SPACE + CUSTOMER
				+ ON + CUSTOMER + DOT + CustomerTable.COLUMN_NAME_CUSTOMER_ID
				+ EQUALS + ORDER + DOT + OrderTable.COLUMN_NAME_CUSTOMER_ID;
		if (sqlSelection != null) {
			sqlQuery += WHERE + sqlSelection;
		}
		if (sqlOrderBy != null) {
			sqlQuery += ORDER_BY + sqlOrderBy;
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
		int orderId = getIntColumn(c, OrderTable.COLUMN_NAME_ORDER_ID);
		long orderDate = getLongColumn(c, OrderTable.COLUMN_NAME_ORDER_DATE);
		String orderNumber = getStringColumn(c,
				OrderTable.COLUMN_NAME_ORDER_NUMBER);
		String idName = getStringColumn(c, OrderTable.COLUMN_NAME_ID_NAME);
		String cemeteryBoard = getStringColumn(c,
				OrderTable.COLUMN_NAME_CEMETERY_BOARD);
		String cemetery = getStringColumn(c, OrderTable.COLUMN_NAME_CEMETERY);
		String cemeteryBlock = getStringColumn(c,
				OrderTable.COLUMN_NAME_CEMETERY_BLOCK);
		String cemeteryNumber = getStringColumn(c,
				OrderTable.COLUMN_NAME_CEMETERY_NUMBER);
		long timeCreated = getLongColumn(c, OrderTable.COLUMN_NAME_TIME_CREATED);
		long timeLastUpdate = getLongColumn(c,
				OrderTable.COLUMN_NAME_TIME_LAST_UPDATE);

		Order order = new Order(orderId, orderNumber, idName, timeCreated,
				timeLastUpdate, cemetery, cemeteryBoard, cemeteryBlock,
				cemeteryNumber, orderDate, getCustomer(c),
				getProducts(orderId), getImages(orderId));

		return order;
	}

	private Image getImage(Cursor c) {
		int imageId = getIntColumn(c, ImageTable.COLUMN_NAME_IMAGE_ID);
		String imagePath = getStringColumn(c, ImageTable.COLUMN_NAME_IMAGE);

		return new Image(imageId, imagePath);
	}

	private Collection<Image> getImages(int orderId) {
		String sqlImages = SELECT_FROM + ImageTable.TABLE_NAME + SPACE + IMAGE
				+ JOIN + OrderTable.TABLE_NAME + SPACE + ORDER + ON + IMAGE
				+ DOT + ImageTable.COLUMN_NAME_ORDER_ID + EQUALS + ORDER + DOT
				+ OrderTable.COLUMN_NAME_ORDER_ID + WHERE + IMAGE + DOT
				+ ImageTable.COLUMN_NAME_ORDER_ID + EQUALS + QUESTION_MARK;

		Cursor c = db.rawQuery(sqlImages,
				new String[] { Integer.toString(orderId) });
		Collection<Image> images = new LinkedList<Image>();

		while (c.moveToNext()) {
			images.add(getImage(c));
		}

		return images;
	}

	private Product getProduct(Cursor c) {
		int productId = getIntColumn(c, ProductTable.COLUMN_NAME_PRODUCT_ID);
		String description = getStringColumn(c,
				ProductTable.COLUMN_NAME_DESCRIPTION);
		String materialColor = getStringColumn(c,
				ProductTable.COLUMN_NAME_MATERIAL_COLOR);
		String frontWork = getStringColumn(c,
				ProductTable.COLUMN_NAME_FRONT_WORK);

		List<Task> tasks = getTasks(c);

		boolean isStone = !c.isNull(c
				.getColumnIndexOrThrow(StoneTable.COLUMN_NAME_PRODUCT_ID));
		if (isStone) {
			return getStone(c, productId, description, materialColor,
					frontWork, tasks);
		} else {
			return new Product(productId, materialColor, description,
					frontWork, tasks, getType(c));
		}
	}

	private Collection<Product> getProducts(int orderId) {
		String sqlProducts = SELECT_FROM + ProductTable.TABLE_NAME + SPACE
				+ PRODUCT + LEFT_JOIN + StoneTable.TABLE_NAME + SPACE + STONE
				+ ON + PRODUCT + DOT + ProductTable.COLUMN_NAME_PRODUCT_ID
				+ EQUALS + STONE + DOT + StoneTable.COLUMN_NAME_PRODUCT_ID
				+ LEFT_JOIN + TaskTable.TABLE_NAME + SPACE + TASK_TO_PRODUCT
				+ ON + PRODUCT + DOT + ProductTable.COLUMN_NAME_PRODUCT_ID
				+ EQUALS + TASK_TO_PRODUCT + DOT
				+ TaskTable.COLUMN_NAME_PRODUCT_ID 
				+ LEFT_JOIN + ProductTypeTable.TABLE_NAME + SPACE + PRODUCT_TYPE
				+ ON + PRODUCT + DOT + ProductTable.COLUMN_NAME_PRODUCT_TYPE_ID 
				+ EQUALS + PRODUCT_TYPE + DOT + ProductTypeTable.COLUMN_NAME_PRODUCT_TYPE_ID
				+ LEFT_JOIN + StationTable.TABLE_NAME + SPACE + TASK + ON + TASK_TO_PRODUCT
				+ DOT + TaskTable.COLUMN_NAME_STATION_ID + EQUALS + TASK + DOT
				+ StationTable.COLUMN_NAME_STATION_ID + WHERE + PRODUCT + DOT
				+ ProductTable.COLUMN_NAME_ORDER_ID + EQUALS + QUESTION_MARK
				+ ORDER_BY + PRODUCT + DOT
				+ ProductTable.COLUMN_NAME_PRODUCT_ID + COMMA_SEP
				+ TASK_TO_PRODUCT + DOT + TaskTable.COLUMN_NAME_SORT_ORDER;

		Cursor c = db.rawQuery(sqlProducts,
				new String[] { Integer.toString(orderId) });
		Collection<Product> products = new LinkedList<Product>();

		while (c.moveToNext()) {
			products.add(getProduct(c));
		}

		return products;
	}
	
	private ProductType getType(Cursor c) {
		int productTypeId = getIntColumn(c, ProductTypeTable.COLUMN_NAME_PRODUCT_TYPE_ID);
		String name = getStringColumn(c, ProductTypeTable.COLUMN_NAME_TYPE);
		
		return new ProductType(productTypeId, name);
	}

	private Stone getStone(Cursor c, int productId, String description,
			String materialColor, String frontWork, List<Task> tasks) {
		String stoneModel = getStringColumn(c,
				StoneTable.COLUMN_NAME_STONE_MODEL);
		String sideBackwork = getStringColumn(c,
				StoneTable.COLUMN_NAME_SIDE_BACK_WORK);
		String textStyle = getStringColumn(c, StoneTable.COLUMN_NAME_TEXTSTYLE);
		String ornament = getStringColumn(c, StoneTable.COLUMN_NAME_ORNAMENT);

		return new Stone(productId, materialColor, description, frontWork,
				tasks, stoneModel, sideBackwork, textStyle, ornament,getType(c));
	}

	private Task getTask(Cursor c) {
		int status = getIntColumn(c, TaskTable.COLUMN_NAME_TASK_STATUS);

		return !c.isNull(c.getColumnIndex(TaskTable.COLUMN_NAME_TASK_STATUS)) ? new Task(
				getStation(c), Status.valueOf(status)) : null;
	}

	private Station getStation(Cursor c) {
		int stationId = getIntColumn(c, StationTable.COLUMN_NAME_STATION_ID);
		String name = getStringColumn(c, StationTable.COLUMN_NAME_STATION);

		return new Station(stationId, name);
	}

	public Collection<Station> getStations() {
		Cursor c = db.query(StationTable.TABLE_NAME, null, null, null, null,
				null, null);
		Collection<Station> stations = new ArrayList<Station>();
		while (c.moveToNext()) {
			stations.add(getStation(c));
		}
		return stations;
	}

	private Collection<Integer> getProductTypeIds() {
		Collection<Integer> ids = new ArrayList<Integer>();
		Cursor c = db.query(ProductTypeTable.TABLE_NAME,
				new String[] { ProductTypeTable.COLUMN_NAME_PRODUCT_TYPE_ID },
				null, null, null, null, null);

		while (c.moveToNext()) {
			ids.add(getIntColumn(c,
					ProductTypeTable.COLUMN_NAME_PRODUCT_TYPE_ID));
		}

		return ids;
	}

	private Collection<Integer> getStationIds() {
		Collection<Integer> ids = new ArrayList<Integer>();
		Cursor c = db.query(StationTable.TABLE_NAME,
				new String[] { StationTable.COLUMN_NAME_STATION_ID }, null,
				null, null, null, null);

		while (c.moveToNext()) {
			ids.add(getIntColumn(c, StationTable.COLUMN_NAME_STATION_ID));
		}

		return ids;
	}

	private List<Task> getTasks(Cursor c) {
		List<Task> tasks = new LinkedList<Task>();
		int productId = getIntColumn(c, ProductTable.COLUMN_NAME_PRODUCT_ID);

		do {
			if (productId != getIntColumn(c,
					ProductTable.COLUMN_NAME_PRODUCT_ID)) {
				break;
			}
			Task task = getTask(c);
			if (task != null) {
				tasks.add(task);
			}
		} while (c.moveToNext());

		c.moveToPrevious();

		return tasks;
	}

	//Gets a column with the specified type, int, long or String
	private int getIntColumn(Cursor c, String columnName) {
		return c.getInt(c.getColumnIndexOrThrow(columnName));
	}

	private long getLongColumn(Cursor c, String columnName) {
		return c.getLong(c.getColumnIndexOrThrow(columnName));
	}

	private String getStringColumn(Cursor c, String columnName) {
		return c.getString(c.getColumnIndexOrThrow(columnName));
	}
}
