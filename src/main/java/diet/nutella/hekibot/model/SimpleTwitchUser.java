package main.java.diet.nutella.hekibot.model;

public class SimpleTwitchUser {
	private int id;
	private String userName;
	
	public SimpleTwitchUser(int id, String userName) {
		this.id = id;
		this.userName = userName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public boolean equals(SimpleTwitchUser user) {
		return this.id == user.getId() && this.userName.equals(user.getUserName());
	}

	@Override
	public String toString() {
		return id + ":" + userName;
	}
	
	
}
