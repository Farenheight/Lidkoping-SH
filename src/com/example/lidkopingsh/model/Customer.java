package com.example.lidkopingsh.model;

/**
 * A class representing a customer.
 * 
 * @author Kim
 * 
 */
public class Customer {
	
	private String title = "";
	private String name;
	private String address;
	private String eMail;
	private int id;
	
	/**
	 * Creates a Customer object.
	 * @param title the customers title
	 * @param name the customers name
	 * @param address the address of the customer
	 * @param eMail the customers email
	 */
	public Customer(String title, String name, String address, String eMail, int id){
		this(name, address, eMail, id);
		this.title = title;
	}
	
	public Customer(String name, String address, String eMail,int id){
		this.name = name;
		this.address = address;
		this.eMail = eMail;
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public String geteMail() {
		return eMail;
	}
	
	public int getId() {
		return id;
	}
}
