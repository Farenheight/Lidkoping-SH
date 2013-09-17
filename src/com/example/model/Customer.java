package com.example.model;

/**
 * A class representing a customer.
 * 
 * @author Kim
 * 
 */
public class Customer {
	
	private String title;
	private String name;
	private String address;
	private String eMail;
	
	/**
	 * Creates a Customer object.
	 * @param title the customers title
	 * @param name the customers name
	 * @param address the address of the customer
	 * @param eMail the customers email
	 */
	public Customer(String title, String name, String address, String eMail){
		this(name, address, eMail);
		this.title = title;
	}
	
	public Customer(String name, String address, String eMail){
		this.name = name;
		this.address = address;
		this.eMail = eMail;
	}
}
