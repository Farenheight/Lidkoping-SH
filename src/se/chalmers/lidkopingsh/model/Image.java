package se.chalmers.lidkopingsh.model;

import se.chalmers.lidkopingsh.util.Syncable;

/**
 * Holds the filepath to an image and its identification number.
 * 
 * @author Alexander Härenstam
 * @author Olliver Mattsson
 * @author Anton
 */
public class Image implements Syncable<Image> {
	private int id;
	private String imagePath;

	public Image(int id, String imagePath) {
		this.id = id;
		this.imagePath = imagePath;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Image i = (Image) o;
		return id == i.id && imagePath.equals(i.imagePath);
	}

	public int getId() {
		return id;
	}

	public String getImagePath() {
		return imagePath.replace("/", "");
	}
	
	public String getServerImagePath() {
		return imagePath;
	}

	@Override
	public int hashCode() {
		return 31 * id + imagePath.hashCode();
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
			this.id = newData.id;
			this.imagePath = newData.imagePath;
			return true;
		}
		return false;
	}
}
