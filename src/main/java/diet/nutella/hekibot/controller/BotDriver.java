package main.java.diet.nutella.hekibot.controller;


import java.util.Properties;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.cap.EnableCapHandler;


import main.java.diet.nutella.hekibot.GetProperties;

public class BotDriver {
	public static Properties props;
	public static String CHANNEL_NAME = "";
	
	private static String[] MOD_LIST = {
			"hekimae",
			"7updiet",
			"alastairch",
			"aske347",
			"bx3waage",
			
			"bengt53",
			"centane",
			"chloelock",
			"henryjiii",
			"khiiess",
			"limeman2",
			"mav3r1ck1989",
			"riggsy99",
			"thebstrike",
			"mindfartio",
			"doh0",
			
			"crazyheartsz_",
			"ignorancetv",
			"rachel_tv",
			"thatrenguy"
	};
	
	private static PircBotX hekiBot;
	private static HekiBotUI ui;
	
	public static String concatNames(String[] names) {
		StringBuilder sb = new StringBuilder();
		if (names.length == 1) return names[0];
		
		for (int i = 0; i < names.length - 1; i++) {
			sb.append(names[i]);
			if (i != names.length - 1) sb.append(",");
			sb.append(" ");
		}
		
		sb.append("and ").append(names[names.length - 1]);
		return sb.toString();
	}
	
	
	
	public static void main(String[] args) { 
		
		//// Set up Properties to be used widely in the project
		props = new GetProperties().getProperties();

		BotDriver.CHANNEL_NAME = props.getProperty("channel-name");
		
		///// Configuration for the PircBotX
		Configuration config = new Configuration.Builder()
				.setAutoNickChange(false)
				.setOnJoinWhoEnabled(false)
				.setCapEnabled(true)
				.addCapHandler(new EnableCapHandler("twitch.tv/membership"))
				.addServer(props.getProperty("irc-host"), 
						Integer.parseInt(props.getProperty("irc-port")))
				.setName("hekibot")
				.setServerPassword(props.getProperty("oAuth"))
				.addListener(new MessageListener())
				.addAutoJoinChannel(CHANNEL_NAME)
				.buildConfiguration();
				
		hekiBot = new PircBotX(config);		
		
		ui = new HekiBotUI(hekiBot);
	}
}
