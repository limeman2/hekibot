package main.java.diet.nutella.hekibot.model;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import main.java.diet.nutella.hekibot.controller.BotDriver;

public class UserDAO {
	
	private static final String UPDATE_STATEMENT = 
			"INSERT INTO users (name, coins, time) VALUES (?, 1, 1) ON DUPLICATE KEY UPDATE time = time + 1;";
	private static final String MODIFY_COINS_STATEMENT = 
			"INSERT INTO users (name, coins, time) VALUES (?, ?, 1) ON DUPLICATE KEY UPDATE coins = coins + ?";
	private static final String USER_REQUEST_URL = String.format(
			"https://tmi.twitch.tv/group/user/%s/chatters", 
			BotDriver.CHANNEL_NAME.substring(1));
	
	private Connection conn;
	
	public UserDAO() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(
					BotDriver.props.getProperty("db-URL"),
					BotDriver.props.getProperty("db-username"), 
					BotDriver.props.getProperty("db-password"));
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addCoinsToUser(String name, int amount) {
		PreparedStatement stmt = null;
		
		try {
			stmt = conn.prepareStatement(MODIFY_COINS_STATEMENT);
			stmt.setString(1, name);
			stmt.setInt(2, amount);
			stmt.setInt(3, amount);
			stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateLoyalty(String name) {
		String converter[] = new String[1];
		converter[0] = name;
		updateLoyalty(converter);
	}
	
	public void updateLoyalty(String[] names) {
		PreparedStatement stmt = null;
		
		try {
			stmt = conn.prepareStatement(UPDATE_STATEMENT);
			for(String name : names) {
				stmt.setString(1, name);
				stmt.executeUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public User getUserFromDatabase(String name) {
		PreparedStatement stmt = null;
		
		try {
			stmt = conn.prepareStatement("SELECT * FROM users WHERE name=?");
			stmt.setString(1, name);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			
			return new User(
					rs.getString(1),
					rs.getInt(2),
					rs.getInt(3)
					);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public String[] getUsersInChannel() {
		List<String> users = new ArrayList<String>();
		try {
			
			JSONParser jp = new JSONParser();
			
			URL url = new URL(USER_REQUEST_URL);
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
			e.printStackTrace();
		}
		return users.toArray(new String[0]);
	}
	
	public String[] getMods() {
		List<String> users = new ArrayList<String>();
		try {
			
			JSONParser jp = new JSONParser();
			
			URL url = new URL(USER_REQUEST_URL);
			HttpURLConnection request = (HttpURLConnection) url.openConnection();
			request.setRequestMethod("GET");
			request.setRequestProperty("Accept", "application/vnd.twitchtv.v5+json");
			request.setRequestProperty("Client-ID", BotDriver.props.getProperty("twitch-api-client-id"));
			request.connect();
			
			///////// Parse JSON data into different user types
			
			JSONObject obj = (JSONObject) jp.parse(new InputStreamReader((InputStream) request.getContent()));
			JSONArray mods = (JSONArray) ((JSONObject)obj.get("chatters")).get("moderators");
		
			///////// Add mods to list
			users.addAll(0, mods);			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return users.toArray(new String[0]);
	}
}
