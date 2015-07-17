public class User {
	public String name;
	public String email;
	public int password;
	public String gender;
	public int age;
	public long Telephone;
	private int score;

	User(String name, String email, int password, String gender, int age, long telephone) {
		this.name = name;
		this.password = password;
		this.email = email;
		this.gender = gender;
		this.age = age;
		this.Telephone = telephone;
		this.score = 0;
	}

	User() {
		this.score = 0;
	}

	public void update(int newscore) {
		this.score = this.score + newscore;
	}

	public int getScore() {
		return this.score;
	}
}
