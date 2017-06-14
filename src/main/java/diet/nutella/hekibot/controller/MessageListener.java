package main.java.diet.nutella.hekibot.controller;

import java.util.Scanner;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

import main.java.diet.nutella.hekibot.model.UserDAO;

public class MessageListener extends ListenerAdapter {
	private static final String HEKICOIN_COMMAND = "!hekicoins";
	private static final String REWARD_COMMAND = "!reward";
	private UserDAO dao;
	
	public MessageListener() {
		dao = new UserDAO();
	}
	
	@Override
	public void onGenericMessage(GenericMessageEvent event) throws Exception {
		String name = event.getUser().getNick();
		if (event.getMessage().equalsIgnoreCase(HEKICOIN_COMMAND)) {
			event.respond(dao.getUserFromDatabase(name).getCoins() + " hekicoins ");
		}
		
		if (event.getMessage().matches(REWARD_COMMAND+" .*")) {
			Scanner scan = new Scanner(event.getMessage());
			scan.useDelimiter(" ");
			scan.next();
			String recipient = scan.next();
			int amount = scan.nextInt();
			
			dao.addCoinsToUser(recipient, amount);
			event.respondWith(recipient + " has just received " + amount + " hekicoins PogChamp ");
			scan.close();
		}
		
		
		//dao.updateLoyalty(users);

		

		
		//OutputIRC irc = new OutputIRC(event.getBot());
		//irc.message(BotDriver.CHANNEL_NAME, event.getMessage());
	}
	
}
