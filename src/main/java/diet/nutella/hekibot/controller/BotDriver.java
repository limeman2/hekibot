package main.java.diet.nutella.hekibot.controller;


import java.util.*;
import java.util.concurrent.TimeUnit;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.UnknownEvent;
import org.pircbotx.output.OutputIRC;

import main.java.diet.nutella.hekibot.GetProperties;
import main.java.diet.nutella.hekibot.loyaltytracker.LoyaltyTracker;
import main.java.diet.nutella.hekibot.model.DatabaseReconnecter;

public class BotDriver {
	public static Properties props;
	public static String CHANNEL_NAME;
	
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
	
	public static String concatNames(Object[] names) {
		StringBuilder sb = new StringBuilder();
		if (names.length == 1) return names[0].toString();
		
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
		props = GetProperties.getConfigProperties();

		// Get Redeem Commands
		Map<String, RedeemCommand> commands = GetProperties.getRedeemCommands();

		BotDriver.CHANNEL_NAME = props.getProperty("channel-name");
		
		///// Get instance of LoyaltyTracker
		LoyaltyTracker.getInstance();

		///// Configuration for the PircBotX
		Configuration config = new Configuration.Builder()
				.setAutoNickChange(false)
				.setOnJoinWhoEnabled(false)
				.setCapEnabled(true)
				.addCapHandler(new EnableCapHandler("twitch.tv/membership"))
				.addCapHandler(new EnableCapHandler("twitch.tv/tags"))
				.addCapHandler(new EnableCapHandler("twitch.tv/commands"))
				.addServer(props.getProperty("irc-host"), 
						Integer.parseInt(props.getProperty("irc-port")))
				.setName("hekibot")
				.setServerPassword(props.getProperty("oAuth"))
				.addListener(new MessageListener(LoyaltyTracker.getInstance().getDAO()))
				.addListener(new ListenerAdapter() {
					///// Simple listener to make sure we notify
					///// users in the channel that we've joined
					@Override
					public void onJoin(JoinEvent event) throws Exception {
						if (event.getUser().getNick().equals("hekibot")) {
							new OutputIRC(event.getBot())
								.message(BotDriver.CHANNEL_NAME, "I just disconnected, but now I'm back! :--) ");
						}
					}
					
				})
				.addListener(new ListenerAdapter() {
					///// Simple listener to reconnect if we disconnect for 
					///// whatever reason
					@Override
					public void onDisconnect(DisconnectEvent event) throws Exception {
						event.getBot().startBot();
					}	
				})
				.addListener(new RedeemCommandListener(commands))
				.addAutoJoinChannel(CHANNEL_NAME)
				.addListener(new SubscriberListener(LoyaltyTracker.getInstance().getDAO()))
				.buildConfiguration();
		
		/// Set up online checker
		OnlineChecker onlineChecker = new OnlineChecker(LoyaltyTracker.getInstance().getDAO());
		Timer onlineTimer = new Timer();
		onlineTimer.scheduleAtFixedRate(onlineChecker, 0, LoyaltyTracker.DEF_PAYOUT_INTERVAL / 2);
		
		/// Set up database reconnecter
		Timer dbTimer = new Timer();
		dbTimer.scheduleAtFixedRate(
				new DatabaseReconnecter(LoyaltyTracker.getInstance().getDAO()), 
				0, 
				1000 * 60 * 30);
		
		hekiBot = new PircBotX(config);

		ui = new HekiBotUI(hekiBot); 
	}
}
