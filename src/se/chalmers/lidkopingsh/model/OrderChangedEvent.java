package se.chalmers.lidkopingsh.model;

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
