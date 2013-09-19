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
	private String postAddress;
	private String eMail;
	
	/**
	 * Creates a Customer object.
	 * @param title the customers title
	 * @param name the customers name
	 * @param address the address of the customer
	 * @param eMail the customers email
	 */
	public Customer(String title, String name, String address, String postAddress, String eMail){
		this(name, address, postAddress, eMail);
		this.title = title;
	}
	
	public Customer(String name, String address, String postAddress, String eMail){
		this.name = name;
		this.address = address;
		this.postAddress = postAddress;
		this.eMail = eMail;
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
	
	public String getPostAddress(){
		return postAddress;
	}

	public String getEMail() {
		return eMail;
	}
}
