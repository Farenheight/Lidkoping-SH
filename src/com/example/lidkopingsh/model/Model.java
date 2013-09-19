package com.example.lidkopingsh.model;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Model {
	private List<Order> orders;
	private List<Listener<Model>> listeners;
	
	public Model(){
		orders = new ArrayList<Order>();
		listeners = new ArrayList<Listener<Model>>();
	}
	
	public void addOrder(Order o){
		orders.add(o);
		notifyModelListeners();
	}
	
	public void removeOrder(Order o){
		orders.remove(o);
		notifyModelListeners();
	}
	
	public void replaceOrders(ArrayList<Order> list){
		orders = list;
		notifyModelListeners();
	}
	
	public Product getProductById(int id) throws NoSuchElementException{
		for(Order o : orders){
			for(Product p : o.getProducts()){
				if(p.getId() == id){
					return p;
				}
			}
		}
		throw new NoSuchElementException();
	}
	
	private void notifyModelListeners() {
		for (Listener<Model> l : listeners) {
			l.changed(this);
		}
	}
	
	/**
	 * Adds an modellistener to this Model listeners.
	 * 
	 * @param listener
	 *            the interested listener for this object
	 */
	public void addOrderListener(Listener<Model> listener) {
		listeners.add(listener);
	}

	/**
	 * Removes an modellistener from this object.
	 * 
	 * @param listener
	 *            the uninterested listener
	 */
	public void removeOrderListener(Listener<Order> listener) {
		listeners.remove(listener);
	}

	public List<Order> getOrders(){
		return orders;
	}
}
