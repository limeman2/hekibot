package main.java.diet.nutella.hekibot.model;

public class User {
	private String name;
	private int coins;
	private int time;
	
	public User(String name, int coins, int time) {
		this.name = name;
		this.coins = coins;
		this.time = time;
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
}
