package com.MVC.Model;

public class User {

	private String name;
	private int score;

	public User() {
		super();
	}

	public User(String name, int score) {
		this.name = name;
		this.score = score;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getName() {
		return this.name;
	}

	public int getScore() {
		return this.score;
	}
}
