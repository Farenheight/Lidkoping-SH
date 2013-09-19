package com.example.lidkopingsh.model;

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
	
	public Stone(List<Task> tasks) {
		super(tasks);
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

}
