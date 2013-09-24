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
	private int id;
	
	/**
	 * Creates a Customer object.
	 * @param title the customers title
	 * @param name the customers name
	 * @param address the address of the customer
	 * @param eMail the customers email
	 */
	public Customer(String title, String name, String address, String postAddress, String eMail, int id){
		this.name = name;
		this.address = address;
		this.postAddress = postAddress;
		this.eMail = eMail;
		this.title = title;
		this.id = id;
	}
	
	/**
	 * Only for testing purposes.
	 */
	public Customer(){
		this("Mr", "Olle Bengtsson", "Anonymgatan 1", "416 00 Göteborg", "anonym@mail.com", 1);
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
	
	public int getId() {
		return id;
	}
	
	public Customer clone(){
		return new Customer(title, name, address, postAddress, eMail, id);
	}
}
