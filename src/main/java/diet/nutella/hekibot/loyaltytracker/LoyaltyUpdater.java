package main.java.diet.nutella.hekibot.loyaltytracker;

import java.util.TimerTask;

import main.java.diet.nutella.hekibot.model.UserDAO;

public class LoyaltyUpdater extends TimerTask {
	
	private UserDAO dao;
	
	public LoyaltyUpdater(UserDAO dao) {
		this.dao = dao;
	}

	@Override
	public void run() {		
		dao.updateLoyalty(dao.getUsersInChannel());
	}
}
