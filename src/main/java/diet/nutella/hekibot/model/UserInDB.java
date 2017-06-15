package main.java.diet.nutella.hekibot.model;

public class UserInDB {
	private int id;
	private String name;
	private int coins;
	private int time;
	
	public UserInDB(int id, String name, int coins, int time) {
		this.id = id;
		this.name = name;
		this.coins = coins;
		this.time = time;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCoins() {
		return coins;
	}

	public void setCoins(int coins) {
		this.coins = coins;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}
	
	public String toString() {
		return "Name: " + name + ", coins: " + coins + ", minutes watched: " + time;
	}
}
