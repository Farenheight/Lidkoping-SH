package com.example.lidkopingsh.model;

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
	
	public Stone(List<Listener<Product>> listeners, List<Task> tasks) {
		super(tasks);
	}
	
	public Stone(){
		this(new ArrayList<Listener<Product>>(), new ArrayList<Task>());
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

}
