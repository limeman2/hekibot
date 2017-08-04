package main.java.diet.nutella.hekibot.model;

import java.sql.SQLException;
import java.util.Random;

public class Gamble {
	private SimpleTwitchUser user;
	private UserDAO dao;
	private long lastGamble = 0;
	private static final int COOLDOWN = 120 * 1000;
	
	public Gamble(SimpleTwitchUser user, UserDAO dao) {
		this.user = user;
		this.dao = dao;
	}
	
	public SimpleTwitchUser getSimpleTwitchUser() {
		return user;
	}
	
	public String execute(int amount) throws SQLException {
		long timeSinceLastGamble = System.currentTimeMillis() - lastGamble;
		UserInDB tempUser;
		
		tempUser = dao.getUserFromDatabase(user.getId());
		
		
		if (amount > tempUser.getCoins() || amount < 1) {
			return tempUser.getName()
					+ ((amount >= 1) ? ": You don't have enough coins to do that." 
							: ": You can't gamble less than 1 hekicoin.");
		
		} else {
			if (timeSinceLastGamble < COOLDOWN) {
				return tempUser.getName() 
						+ ": You still have a " 
						+ (COOLDOWN - timeSinceLastGamble) / 1000 
						+ " second cooldown on gambling.";
			} else {
				Random random = new Random();
				
				int result = random.nextInt(100) + 1;
				lastGamble = System.currentTimeMillis();
				
				if(result == 100) {
					dao.addCoinsToUser(tempUser.getName(), amount * 9);
					return tempUser.getName()
							+ " just rolled "
							+ result
							+ " and won "
							+ amount * 10
							+ " hekicoins!!! (10x) PogChamp ";
				} else if (result >= 65) {
					dao.addCoinsToUser(tempUser.getName(), amount);
					return tempUser.getName()
							+ " just rolled "
							+ result
							+ " and won "
							+ amount
							+ " hekicoins! (2x) SeemsGood ";
					
				} else {
					dao.addCoinsToUser(tempUser.getName(), -amount);
					return tempUser.getName()
							+ " just rolled "
							+ result
							+ " and lost "
							+ amount
							+ " hekicoins! FeelsBadMan ";
				}	
			}
		}
	}
}
