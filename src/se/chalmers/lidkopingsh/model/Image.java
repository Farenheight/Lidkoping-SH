package se.chalmers.lidkopingsh.model;

import java.io.File;

import se.chalmers.lidkopingsh.util.Syncable;

/**
 * Holds the filepath to an image and its identification number.
 * 
 * @author Alexander H�renstam
 * @author Olliver Mattsson
 */
public class Image implements Syncable<Image> {
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

	@Override
	public boolean sync(Image newData) {
		if (newData != null && this.id == newData.id
				&& getClass() == newData.getClass()) {
			} else {
				return this.id == newData.id && this.imagePath.equals(newData.imagePath);
			}
		return false;
	}
}
