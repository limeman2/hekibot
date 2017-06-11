package main.java.diet.nutella.hekibot;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class LoyaltyUpdater extends TimerTask {
	
	private static final String SQL_STATEMENT1 = "INSERT INTO users (name, coins, time) VALUES (";
	private static final String SQL_STATEMENT2 = ", 1, 1) ON DUPLICATE KEY UPDATE time = time + 1;";

	@Override
	public void run() {		
		        
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(
					BotDriver.props.getProperty("db-URL"),
					BotDriver.props.getProperty("db-username"), 
					BotDriver.props.getProperty("db-password"));
			
			for(String s : getUsers()) {
				stmt = conn.prepareStatement(SQL_STATEMENT1 + "?" + SQL_STATEMENT2);
				stmt.setString(1, s);
				stmt.executeUpdate();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String[] getUsers() {
		List<String> users = new ArrayList<String>();
		try {
			
			JSONParser jp = new JSONParser();
			
			URL url = new URL(String.format(
					"https://tmi.twitch.tv/group/user/%s/chatters", 
					BotDriver.CHANNEL_NAME.substring(1)));
			HttpURLConnection request = (HttpURLConnection) url.openConnection();
			request.setRequestMethod("GET");
			request.setRequestProperty("Accept", "application/vnd.twitchtv.v5+json");
			request.setRequestProperty("Client-ID", BotDriver.props.getProperty("twitch-api-client-id"));
			request.connect();
			
			///////// Parse JSON data into different user types
			
			JSONObject obj = (JSONObject) jp.parse(new InputStreamReader((InputStream) request.getContent()));
			JSONArray staff = (JSONArray) ((JSONObject)obj.get("chatters")).get("staff");
			JSONArray viewers = (JSONArray) ((JSONObject)obj.get("chatters")).get("viewers");
			JSONArray globalMods = (JSONArray) ((JSONObject)obj.get("chatters")).get("global_mods");
			JSONArray mods = (JSONArray) ((JSONObject)obj.get("chatters")).get("moderators");
			JSONArray admins = (JSONArray) ((JSONObject)obj.get("chatters")).get("admins");
			
			///////// Add every user type to one big list
			
			users.addAll(0, staff);
			users.addAll(0, viewers);
			users.addAll(0, globalMods);
			users.addAll(0, mods);
			users.addAll(0, admins);			
		} catch (Exception e) {
			
		}
		return users.toArray(new String[0]);
	}

}
