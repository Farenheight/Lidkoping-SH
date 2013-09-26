package com.example.lidkopingsh.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.example.lidkopingsh.model.Customer;
import com.example.lidkopingsh.model.Order;
import com.example.lidkopingsh.model.Product;
import com.example.lidkopingsh.model.Status;
import com.example.lidkopingsh.model.Stone;
import com.example.lidkopingsh.model.Task;

public class OrderDbFiller {
	private static int customerId = 500;
	private static int orderId = 1;
	private static int orderNumber = 130001;
	private static int productId = 400;
	private static int taskId = 600;
	public static void fillDb(OrderDbStorage db){

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

	private static Customer getCustomer() {
		return new Customer("Mr", "Namn Efternamn", "Adress gata 5",
				"123 45 Stad", "email@test.se", customerId++);
	}

	private static Order getOrder(String idName, Customer customer) {
		return new Order(orderId++, String.valueOf(orderNumber++), idName,
				System.currentTimeMillis(), System.currentTimeMillis(),
				"Kyrkogård", "Kyrkogårdsnämnd", "Notation",
				System.currentTimeMillis(), customer, null);
	}

	private static Product getSocle(String description, List<Task> tasks) {
		return new Product(productId++, "Hallandia", description, "Polerad", tasks);
	}

	private static List<Task> getSocleTasks() {
		List<Task> tasks2 = new ArrayList<Task>();
		tasks2.add(new Task(taskId++, "Sågning", Status.DONE));
		tasks2.add(new Task(taskId++, "Slipning"));
		return tasks2;
	}

	private static Stone getStone(String description, List<Task> tasks) {
		return new Stone(productId++, "Hallandia", description, "Polerad", tasks,
				"NB 49", "Råhugget", "Helvetica nedhuggen i guld",
				"Blomma nedhuggen i guld");
	}

	private static List<Task> getStoneTasks() {
		List<Task> tasks = new ArrayList<Task>();
		tasks.add(new Task(taskId++, "Sågning", Status.DONE));
		tasks.add(new Task(taskId++, "Råhuggning"));
		tasks.add(new Task(taskId++, "Gravering"));
		tasks.add(new Task(taskId++, "Målning"));
		return tasks;
	}
}
