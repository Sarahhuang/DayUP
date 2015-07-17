package com.MVC.Model;

public class Friend {
	private String name;
//	private String email;
//	private String age;
//	private String phone;
//	private String gender;
	private String score;

	public Friend(String name, String score) {
		this.name = name;
		this.score = score;
//		this.email = email;
//		this.age = age;
//		this.phone = phone;
//		this.gender = gender;
	}

	public String getName() {
		return this.name;
	}

	public int getScore() {
		return Integer.parseInt(this.score);
	}
	
//	public int getAge() {
//		return Integer.parseInt(this.age);
//	}
//	
//	public String getEmail() {
//		return this.email;
//	}
//	
//	public String getPhone() {
//		return this.phone;
//	}
//	
//	public String getGender() {
//		return this.gender;
//	}
}
