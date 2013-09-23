package com.example.lidkopingsh.model;

public enum Status {
	DONE(0),NOT_DONE(1);
	private Status(int id){
		this.id = id;
	}
	private int id;
}
