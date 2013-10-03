package se.chalmers.lidkopingsh.model;

/**
 * Specifies an identification number and a name for a certain type of product.
 * 
 * @author Alexander H�renstam
 * @author Olliver Mattsson
 */
public class ProductType {
	private int id;
	private String name;
	
	public ProductType(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
