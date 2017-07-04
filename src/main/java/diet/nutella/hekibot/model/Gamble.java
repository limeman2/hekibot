package main.java.diet.nutella.hekibot.model;

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
	
	public String execute(int amount) {
		long timeSinceLastGamble = System.currentTimeMillis() - lastGamble;
		System.out.println(timeSinceLastGamble);
		UserInDB tempUser = dao.getUserFromDatabase(user.getId());
		if (amount > tempUser.getCoins()) {
			return tempUser.getName()
					+ ": You don't have enough coins to do that.";
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
