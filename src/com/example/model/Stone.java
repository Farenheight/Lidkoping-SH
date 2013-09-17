package com.example.model;

import java.util.List;

/**
 * A class holding the information of a stone. A stone is a 
 * @author Robin Gronberg
 *
 */
public class Stone extends Product{

	public Stone(List<Listener<Product>> listeners, List<Task> tasks) {
		super(tasks);
	}
	private String stoneModel;
	private String 	sideBackWork;
	private String 	textStyle;
	private String 	ornament;

}
