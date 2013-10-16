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
	private File imageFile;
	
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

	public File getImageFile() {
		return imageFile;
	}

	public void setImageFile(File imageFile) {
		this.imageFile = imageFile;
	}
	
	public void deleteImage() {
		if (imageFile != null) {
			imageFile.delete();
			imageFile = null;
		}
	}

	@Override
	public boolean sync(Image newData) {
		if (newData != null && this.id == newData.id
				&& getClass() == newData.getClass()) {
			} else {
				this.id = newData.id;
				this.imagePath = newData.imagePath;
				return true;
			}
		return false;
	}
}
