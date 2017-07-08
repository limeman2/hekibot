package main.java.diet.nutella.hekibot.controller;

import java.util.ArrayList;
import java.util.List;

import main.java.diet.nutella.hekibot.model.Gamble;
import main.java.diet.nutella.hekibot.model.SimpleTwitchUser;

public class GambleTracker {
	private List<Gamble> gambles;
	
	public GambleTracker() {
		this.gambles = new ArrayList<Gamble>();
	}
	
	public void addGamble(Gamble gamble) {
		if(gamble != null) {
			if (!this.contains(gamble.getSimpleTwitchUser())) {
				gambles.add(gamble);
			}
		}
	}
	
	public Gamble getGamble(SimpleTwitchUser user) {
		for (int i = 0; i < gambles.size(); i++) {
			if (gambles.get(i).getSimpleTwitchUser().equals(user)) {
				return gambles.get(i);
			}
		}
		
		return null;
	}
	
	public boolean contains(SimpleTwitchUser usr) {
		for (Gamble g : gambles) {
			if (g.getSimpleTwitchUser().equals(usr)) {
				return true;
			}
		}
		return false;
	}
	
}
