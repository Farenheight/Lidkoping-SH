package com.example.lidkopingsh.model;

import java.util.ArrayList;
import java.util.List;

public class ExampleLayer implements ILayer {

	@Override
	public void changed(Order object) {

	}

	@Override
	public IModel getModel() {
		IModel m = new Model();
		for (int i = 0; i < 100; i++) {
			List<Task> tList = new ArrayList<Task>();
			tList.add(new Task(1, "S�gning"));
			tList.add(new Task(2, "Polering"));
			tList.add(new Task(3, "Gravering"));
			tList.add(new Task(4, "Montering"));
			tList.add(new Task(3, "Gravering"));
			tList.add(new Task(4, "Montering"));
			Order o = new Order();
			o.addProduct(new Stone(1, "Hallandia-granit",
					"Gravv�rd 80x65 cm\nPolerande blomlister 90x40 cm",
					"Framsidan & sockelns ovansida polerad ,matt fas", tList,
					"NB 46", "Polerade", "Sx358-nedhuggen i guld",
					"Kors & sol i guld\nF�glar & blommor vita"));
			m.addOrder(o);
		}
		return m;
	}

	public static void main(String args[]) {
	//	Model m = (Model) ModelHandler.getModel();
//		System.out.println(m.getOrders().get(4).getId());
	}
}
