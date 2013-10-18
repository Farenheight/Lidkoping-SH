package se.chalmers.lidkopingsh.model;

/**
 * An event that holds the order, product and task that has been affected by a change
 * @author Olliver Mattson
 *
 */

public class OrderChangedEvent {
	private Order order;
	private Product product;
	private Task task;
	
	public OrderChangedEvent(Order order, Product product, Task task) {
		this.order = order;
		this.product = product;
		this.task = task;
	}
	
	public Order getOrder() {
		return order;
	}

	public Product getProduct() {
		return product;
	}

	public Task getTask() {
		return task;
	}
	
}
