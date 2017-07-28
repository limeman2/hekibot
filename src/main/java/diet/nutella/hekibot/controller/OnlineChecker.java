package main.java.diet.nutella.hekibot.controller;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.TimerTask;

import main.java.diet.nutella.hekibot.loyaltytracker.LoyaltyTracker;
import main.java.diet.nutella.hekibot.model.UserDAO;

public class OnlineChecker extends TimerTask {
	private UserDAO userDAO;
	
	public OnlineChecker(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Override
	public void run() {
		try {
			////////// Connect to twitch API to check whether channel is online
			
			URL url = new URL("https://api.twitch.tv/kraken/streams/" + BotDriver.props.getProperty("heki-id"));
			HttpURLConnection request = (HttpURLConnection) url.openConnection();
			request.setRequestMethod("GET");
			request.setRequestProperty("Accept", "application/vnd.twitchtv.v5+json");
			request.setRequestProperty("Client-ID", BotDriver.props.getProperty("twitch-api-client-id"));
			request.connect();
			
			InputStream is = (InputStream) request.getContent();
			Scanner scan = new Scanner(is);
			
			/////////// Check if stream is online
			
			boolean streamOnline = !scan.next().equals("{\"stream\":null}");
			LoyaltyTracker.getInstance().setStreamOnline(streamOnline);
			
			boolean isTracking = LoyaltyTracker.getInstance().isTracking();
			
			if (streamOnline) {
				userDAO.connect();
				if (!isTracking) {
					LoyaltyTracker.getInstance().trackLoyalty(true); 	//// Start tracking loyalty
				}
			} else {
				userDAO.disconnect();
				if (isTracking) {
					LoyaltyTracker.getInstance().trackLoyalty(false);	//// Stop tracking loyalty
				}
			}
			
			scan.close();
		} catch (Exception e) {
			
		}
	} 
}
