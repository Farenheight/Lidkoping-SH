package se.chalmers.lidkopingsh.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import se.chalmers.lidkopingsh.model.Customer;
import se.chalmers.lidkopingsh.model.Image;
import se.chalmers.lidkopingsh.model.Order;
import se.chalmers.lidkopingsh.model.Product;
import se.chalmers.lidkopingsh.model.Station;
import se.chalmers.lidkopingsh.model.Status;
import se.chalmers.lidkopingsh.model.Stone;
import se.chalmers.lidkopingsh.model.Task;


public class OrderDbFiller {
	private static int customerId = 500;
	private static int orderId = 1;
	private static int orderNumber = 130001;
	private static int productId = 400;
	private static int charcode = 65;
	private static int imageId = 600;
	
	public static void fillDb(OrderDbStorage db){
		for(int i=0;i<20;i++){
			db.insert(getOrderFullyPopulated("O." + (char)charcode++ + "."));
			db.insert(getOrderWithNullFields("O." + (char)charcode++ + "."));
			db.insert(getOrderWithoutTasks("O." + (char)charcode++ + "."));
		}
	}
	public static Order getOrderFullyPopulated(String idName) {
		Order order = getOrder(idName, getCustomer());

		Collection<Product> products = new LinkedList<Product>();
		products.add(getStone("Beskrivning", getStoneTasks()));
		products.add(getSocle("Sockel under mark", getSocleTasks()));
		order.addProducts(products);

		return order;
	}

	public static Order getOrderWithNullFields(String idName) {
		Order order = getOrder(idName, getCustomer());

		Collection<Product> products = new LinkedList<Product>();
		products.add(getStone(null, getStoneTasks()));
		products.add(getSocle(null, getSocleTasks()));
		order.addProducts(products);

		return order;
	}

	public static Order getOrderWithoutTasks(String idName) {
		Order order = getOrder(idName, getCustomer());

		Collection<Product> products = new LinkedList<Product>();
		products.add(getStone(null, null));
		products.add(getSocle(null, null));
		order.addProducts(products);

		return order;
	}
	
	public static Order getOrderWithoutProducts(String idName) {
		return getOrder(idName, getCustomer());
	}

	private static Customer getCustomer() {
		return new Customer("Mr", "Namn Efternamn", "Adress gata 5",
				"123 45 Stad", "email@test.se", customerId++);
	}

	private static Order getOrder(String idName, Customer customer) {
		return new Order(orderId++, String.valueOf(orderNumber++), idName,
				System.currentTimeMillis(), System.currentTimeMillis(),
				"Kyrkogård", "Kyrkogårdsnämnd", "Kvarter", "Nummer",
				System.currentTimeMillis(), customer, null, getImages());
	}

	private static Collection<Image> getImages() {
		Collection<Image> images = new LinkedList<Image>();
		images.add(new Image(imageId++, "/path"));
		return images;
	}
	private static Product getSocle(String description, List<Task> tasks) {
		return new Product(productId++, "Hallandia", description, "Polerad", tasks);
	}

	private static List<Task> getSocleTasks() {
		List<Task> tasks2 = new ArrayList<Task>();
		tasks2.add(new Task(new Station(1, "Sagning"), Status.DONE));
		tasks2.add(new Task(new Station(2, "Slipning")));
		return tasks2;
	}

	public static Stone getStone(String description, List<Task> tasks) {
		return new Stone(productId++, "Hallandia", description, "Polerad", tasks,
				"NB 49", "Råhugget", "Helvetica nedhuggen i guld",
				"Blomma nedhuggen i guld");
	}

	public static List<Task> getStoneTasks() {
		List<Task> tasks = new ArrayList<Task>();
		tasks.add(new Task(new Station(1, "Sagning"), Status.DONE));
		tasks.add(new Task(new Station(3, "Rahuggning")));
		tasks.add(new Task(new Station(4, "Gravering")));
		tasks.add(new Task(new Station(5, "Malning")));
		return tasks;
	}
}
