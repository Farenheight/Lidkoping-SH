package se.chalmers.lidkopingsh.model;

public class Image {
	private int id;
	private String imagePath;
	
	public Image(int id, String imagePath){
		this.id = id;
		this.imagePath = imagePath;
	}
	
	public int getId() {
		return id;
	}
	
	public String getImagePath() {
		return imagePath;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
}
