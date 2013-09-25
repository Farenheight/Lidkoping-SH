package com.example.lidkopingsh.model;

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
	private String postAddress;
	private String eMail;
	private int id;

	/**
	 * Creates a Customer object.
	 * 
	 * @param title
	 *            the customers title
	 * @param name
	 *            the customers name
	 * @param address
	 *            the address of the customer
	 * @param eMail
	 *            the customers email
	 */
	public Customer(String title, String name, String address,
			String postAddress, String eMail, int id) {
		this.name = name != null ? name : "";
		this.address = address != null ? address : "";
		this.postAddress = postAddress != null ? postAddress : "";
		this.eMail = eMail != null ? eMail : "";
		this.title = title != null ? title : "";
		this.id = id;
	}

	/**
	 * Only for testing purposes.
	 */
	public Customer() {
		this("Mr", "Olle Bengtsson", "Anonymgatan 1", "416 00 Göteborg",
				"anonym@mail.com", 1);
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

	public String getPostAddress() {
		return postAddress;
	}

	public String getEMail() {
		return eMail;
	}

	public int getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o == null || o.getClass() != getClass()) {
			return false;
		} else {
			Customer c = (Customer) o;
			return c.id == c.getId() && this.title.equals(c.getTitle())
					&& this.name.equals(c.getName())
					&& this.address.equals(c.getAddress())
					&& this.postAddress.equals(c.getPostAddress())
					&& this.eMail.equals(c.getEMail());
		}
	}

	public Customer clone() {
		return new Customer(title, name, address, postAddress, eMail, id);
	}
}
