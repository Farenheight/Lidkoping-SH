package se.chalmers.lidkopingsh.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A class holding the information of a stone. A stone is a
 * 
 * @author Robin Gronberg
 * 
 */
public class Stone extends Product {
	private String stoneModel;
	private String sideBackWork;
	private String textStyle;
	private String ornament;

	/**
	 * /** Create a new Product
	 * 
	 * @param id
	 *            The id of the Product (from the database). The id should be
	 *            unique for each element
	 * @param materialColor
	 *            The matierial and color for this Product.
	 * @param description
	 *            The description for this Product.
	 * @param frontWork
	 *            The frontWork for this product
	 * @param tasks
	 *            The tasks this Product should go through for this product to
	 *            be completed.
	 * @param stoneModel
	 *            The type of stone model this stone is.
	 * @param sideToBackWork
	 *            What kind of side and back work.
	 * @param textStyle
	 *            The type of text and style this stone should have
	 * @param ornament
	 *            The ornament this stone should have (if any)
	 */
	public Stone(int id, String materialColor, String description,
			String frontWork, List<Task> tasks, String stoneModel,
			String sideToBackWork, String textStyle, String ornament, ProductType type) {
		super(id, materialColor, description, frontWork, tasks, type);
		this.stoneModel = stoneModel != null? stoneModel : "";
		this.sideBackWork = sideToBackWork != null? sideToBackWork : "";
		this.textStyle = textStyle != null? textStyle : "";
		this.ornament = ornament != null? ornament : "";
	}

	/**
	 * Create a new Stone with Dummy data
	 */
	public Stone() {
		this(1, "Hallandia-granit",
				"Gravv�rd 80x65 cm\nPolerande blomlister 90x40 cm",
				"Framsidan & sockelns ovansida polerad ,matt fas",
				new ArrayList<Task>(), "NB 46", "Polerade",
				"Sx358-nedhuggen i guld",
				"Kors & sol i guld\nF�glar & blommor vita", new ProductType(1, "Sten"));
	}

	public String getStoneModel() {
		return stoneModel;
	}

	public String getSideBackWork() {
		return sideBackWork;
	}

	public String getTextStyle() {
		return textStyle;
	}

	public String getOrnament() {
		return ornament;
	}

	@Override
	public boolean sync(Product data) {
		if (super.sync(data)) {
			Stone newData = (Stone) data;
			this.stoneModel = newData.stoneModel;
			this.sideBackWork = newData.sideBackWork;
			this.textStyle = newData.textStyle;
			this.ornament = newData.ornament;
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Stone){
			Stone s = ((Stone) o);
			return super.equals(s) && this.stoneModel.equals(s.getStoneModel())
					&& this.sideBackWork.equals(s.getSideBackWork())
					&& this.textStyle.equals(s.getTextStyle())
					&& this.ornament.equals(s.getOrnament());
		}else{
			return false;
		}
	}
}
