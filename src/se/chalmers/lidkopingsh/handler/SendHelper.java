package se.chalmers.lidkopingsh.handler;

import se.chalmers.lidkopingsh.model.Order;

public class SendHelper {
	private Order order;
	private ServerLayer serverLayer;
	
	public SendHelper(Order order, ServerLayer serverLayer) {
		this.order = order;
		this.serverLayer = serverLayer;
	}

	public Order getOrder() {
		return order;
	}

	public ServerLayer getServerLayer() {
		return serverLayer;
	}

}
