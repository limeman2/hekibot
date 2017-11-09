package main.java.diet.nutella.hekibot.controller;

import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

import main.java.diet.nutella.hekibot.model.Gamble;
import main.java.diet.nutella.hekibot.model.SimpleTwitchUser;
import main.java.diet.nutella.hekibot.model.TwitchIdAPIRequester;
import main.java.diet.nutella.hekibot.model.UserDAO;
import main.java.diet.nutella.hekibot.model.UserInDB;

public class MessageListener extends ListenerAdapter {
	private static final String HEKICOIN_COMMAND = "!hekicoins";
	private static final String OWN_TIME_COMMAND = "!time";
	private static final String TOP_10_COMMAND = "!top10";
	private static final String HOF_COMMAND = "!hof";
	private static final String REWARD_COMMAND = "!reward";
	private static final String GAMBLE_COMMAND = "!gamble";
	private UserDAO dao;
	private GambleTracker tracker;
	
	public MessageListener(UserDAO dao) {
		this.dao = dao;
		tracker = new GambleTracker();
	}

	@Override
	public void onGenericMessage(GenericMessageEvent event) {
		String name = event.getUser().getNick();

		if (event.getMessage().equalsIgnoreCase((OWN_TIME_COMMAND))) {
			try {
				event.respond(dao.getUserFromDatabase(name).getTime() + " minutes ");
			} catch (SQLException e) {
				event.respond("Something went wrong, sorry. ");
				e.printStackTrace();
			}
		}

		/// Hekicoin command
		/// Respond with the amount of hekicoins the sender has
		if (event.getMessage().equalsIgnoreCase(HEKICOIN_COMMAND)) {
			try {
				event.respond(dao.getUserFromDatabase(name).getCoins() + " hekicoins ");
			} catch (SQLException e) {
				dao.connect();
				try {
					event.respond(dao.getUserFromDatabase(name).getCoins() + " hekicoins ");
				} catch (SQLException e1) {
					e1.printStackTrace();
					System.out.println("ATTENTION, mysql exception AFTER reconnection");
				}
			}
		}
		
		/// Top 10 command
		/// Respond with the top 10 users in DB in terms of coins
		if (event.getMessage().equalsIgnoreCase(TOP_10_COMMAND)) {
			/// Super rare edge case: if we have less than 10 entries in the database
			/// and the top 10 command only returns < 10 entries, check how many
			/// entries were actually returned
			int actualLength = 0;
			
			UserInDB[] top10 = null;
			try {
				top10 = dao.getTopUsers(10);
			} catch (SQLException e) {
				dao.connect();
				try {
					top10 = dao.getTopUsers(10);
				} catch (SQLException e1) {
					e1.printStackTrace();
					System.out.println("ATTENTION, mysql exception AFTER reconnection");
				}
			}
			for (UserInDB usr : top10) {
				if (usr != null) actualLength++;
			}
			String[] response = new String[actualLength];
			
			for(int i = 0; i < response.length; i++) {
				if (top10[i] == null) break;
				response[i] = (i + 1) + " - " + top10[i].getName() + " " + top10[i].getCoins() + " hc";
			}
			
			event.respondWith(org.apache.commons.lang3.StringUtils.join(response, ", "));
		}
		
		/// Hall of Fame command
		/// Respond with the top 10 users in DB in terms of time spent
		/// watching the stream
		if (event.getMessage().equalsIgnoreCase(HOF_COMMAND)) {
			/// Super rare edge case: if we have less than 10 entries in the database
			/// and the top 10 command only returns < 10 entries, check how many
			/// entries were actually returned
			int actualLength = 0;
			
			UserInDB[] top10 = null;
			try {
				top10 = dao.getTopUsersByTime(10);
			} catch (SQLException e) {
				dao.connect();
				try {
					top10 = dao.getTopUsersByTime(10);
				} catch (SQLException e1) {
					e1.printStackTrace();
					System.out.println("ATTENTION, mysql exception AFTER reconnection");
				}
			}
			for (UserInDB usr : top10) {
				if (usr != null) actualLength++;
			}
			String[] response = new String[actualLength];
			
			for(int i = 0; i < response.length; i++) {
				if (top10[i] == null) break;
				response[i] = (i + 1) + " - " + top10[i].getName() + " " + top10[i].getTime() + " mins";
			}
			
			event.respondWith(org.apache.commons.lang3.StringUtils.join(response, ", "));
		}
		
		/// Reward command
		/// Syntax: !reward [user] [amount]
		/// Rewards user [user] with [amount] amount of hekicoins
		if (event.getMessage().matches(REWARD_COMMAND + " .*")) {
			if (event.getUser().getNick().equalsIgnoreCase("limeman2") ||
					event.getUser().getNick().equalsIgnoreCase("hekimae")) {
				Scanner scan = new Scanner(event.getMessage());
				scan.useDelimiter(" ");
				scan.next();
				String recipient = scan.next();
				int amount = scan.nextInt();
				
				try {
					dao.addCoinsToUser(recipient, amount);
				} catch (SQLException e) {
					dao.connect();
					try {
						dao.addCoinsToUser(recipient, amount);
					} catch (Exception ex) {
						
					}
				}
				event.respondWith(recipient + " has just received " + amount + " hekicoins PogChamp ");
				scan.close();
			}
		}
		
		/// Gamble command
		/// Syntax: !gamble [amount]
		/// If you have coins > [amount], gamble [amount] amount of coins
		/// Has a 3 minute cooldown
		if (event.getMessage().matches(GAMBLE_COMMAND + " .*")) {
			try {
				Scanner scan = new Scanner(event.getMessage());
				scan.useDelimiter(" ");
				scan.next();
				int amount = scan.nextInt();
				
				SimpleTwitchUser user = 
						new TwitchIdAPIRequester(event.getUser().getNick()).call()[0];
				
				Gamble storedGamble = tracker.getGamble(user);
				
				if (storedGamble == null) {
					Gamble gamble = new Gamble(user, dao);
					tracker.addGamble(gamble);
					try {
						event.respondWith(gamble.execute(amount));
					} catch (SQLException sqlex) {
						sqlex.printStackTrace();
						System.out.println("MySQL exception. Reconnecting.");
						this.dao.connect();
						try {
							event.respondWith(gamble.execute(amount));
						} catch (SQLException e) {
							e.printStackTrace();
							System.out.println("ATTENTION, mysql exception AFTER reconnection");
						}
					}
					
				} else {
					try {
						event.respondWith(storedGamble.execute(amount));
					} catch (SQLException sqlex) {
						sqlex.printStackTrace();
						System.out.println("MySQL exception. Reconnecting.");
						this.dao.connect();
						try {
							event.respondWith(storedGamble.execute(amount));
						} catch (SQLException e) {
							e.printStackTrace();
							System.out.println("ATTENTION, mysql exception AFTER reconnection");
						}
					}
				}
				
				scan.close();
			} catch (InputMismatchException ex) {
				ex.printStackTrace();
			}
			
		}
	}
	
}
