package main.java.diet.nutella.hekibot;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.TimerTask;

public class OnlineChecker extends TimerTask {
	

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
			
			boolean isTracking = LoyaltyTracker.getInstance().isTracking();
			
			if (streamOnline && !isTracking) {
				LoyaltyTracker.getInstance().trackLoyalty(true); 	//// Start tracking loyalty
			} else if (!streamOnline && isTracking) {
				LoyaltyTracker.getInstance().trackLoyalty(false);	//// Stop tracking loyalty
			}
			
			scan.close();
		} catch (Exception e) {
			
		}
	} 
}
