package main.java.diet.nutella.hekibot.loyaltytracker;

import java.util.TimerTask;

import main.java.diet.nutella.hekibot.model.SimpleTwitchUser;
import main.java.diet.nutella.hekibot.model.UserDAO;

public class LoyaltyUpdater extends TimerTask {
	private UserDAO dao;
	private CurrentUsersTracker currentUsers;
	
	public LoyaltyUpdater(UserDAO dao, CurrentUsersTracker currentUsers) {
		this.dao = dao;
		this.currentUsers = currentUsers;
	}

	@Override
	public void run() {	
		currentUsers.update();
		SimpleTwitchUser[] users = currentUsers.getUsers();
		
		dao.updateLoyalty(users);
	}
}
