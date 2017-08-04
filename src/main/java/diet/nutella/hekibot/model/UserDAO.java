package main.java.diet.nutella.hekibot.model;

import java.io.IOException;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import main.java.diet.nutella.hekibot.controller.BotDriver;

public class UserDAO {
	
	private static final String UPDATE_FULL_USER_STATEMENT =
			"INSERT INTO users "
			+ "(id, name, coins, time) "
			+ "VALUES (?, ?, ?, ?) "
			+ "ON DUPLICATE KEY UPDATE "
			+ "name = ?, "
			+ "coins = ?, "
			+ "time = ?;";
	
	private static final String UPDATE_STATEMENT = 
			"INSERT INTO users "
			+ "(id, name, coins, time) "
			+ "VALUES (?, ?, 1, 1) "
			+ "ON DUPLICATE KEY UPDATE "
			+ "coins = coins + 1,"
			+ "time = time + 1,"
			+ "name = ?;";
	
	private static final String MODIFY_COINS_STATEMENT = 
			"UPDATE users SET coins = coins + ? WHERE name = ?";
	
	private static final String GET_USER_BY_NAME_STATEMENT = 
			"SELECT id, name, coins, time FROM users WHERE name=?";
	
	private static final String GET_USER_BY_ID_STATEMENT =
			"SELECT id, name, coins, time FROM users WHERE id=?";
	
	private static final String GET_TOP_USERS_STATEMENT =
			"SELECT id, name, coins, time FROM users ORDER BY coins DESC LIMIT ?";
	
	private static final String GET_TOP_USERS_BY_TIME_STATEMENT =
			"SELECT id, name, coins, time FROM users ORDER BY time DESC LIMIT ?";
	
	private static final String USER_REQUEST_URL = String.format(
			"https://tmi.twitch.tv/group/user/%s/chatters", 
			BotDriver.CHANNEL_NAME.substring(1));

	
	private Connection conn;
	
	private boolean isConnected;
	
	public UserDAO() {
		this.isConnected = false;
		connect();
	}
	
	public void connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(
					BotDriver.props.getProperty("db-URL"),
					BotDriver.props.getProperty("db-username"), 
					BotDriver.props.getProperty("db-password"));
			this.isConnected = true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace(); 
		} catch (SQLException e) {
			System.out.println("EXCEPTION on " + new Date());
			e.printStackTrace();
			this.isConnected = false;
		}
	}
	
	public void disconnect() {
		try {
			conn.close();
			this.isConnected = false;
		} catch (SQLException e) {
			System.out.println("EXCEPTION on " + new Date());
			e.printStackTrace();
		}
	}
	
	public void addCoinsToUser(String name, int amount) throws SQLException {
		PreparedStatement stmt = null;
		
		stmt = conn.prepareStatement(MODIFY_COINS_STATEMENT);
		stmt.setInt(1, amount);
		stmt.setString(2, name);

		stmt.executeUpdate();
	}
	
	public void updateUser(UserInDB user) throws SQLException {
		PreparedStatement stmt = null;
		
		stmt = conn.prepareStatement(UPDATE_FULL_USER_STATEMENT);
		stmt.setInt(1, user.getId());
		stmt.setString(2, user.getName());
		stmt.setInt(3, user.getCoins());
		stmt.setInt(4, user.getTime());
		
		stmt.setString(5, user.getName());
		stmt.setInt(6, user.getCoins());
		stmt.setInt(7, user.getTime());
		
		stmt.executeUpdate();
	}
	
	public void updateLoyalty(SimpleTwitchUser user) throws SQLException {
		SimpleTwitchUser[] converter = new SimpleTwitchUser[1];
		converter[0] = user;
		updateLoyalty(converter);
	}
	
	public void updateLoyalty(SimpleTwitchUser[] users) throws SQLException {
		PreparedStatement stmt = null;
		
		stmt = conn.prepareStatement(UPDATE_STATEMENT);
		for(SimpleTwitchUser user : users) {
			stmt.setInt(1, user.getId());
			stmt.setString(2, user.getUserName());
			stmt.setString(3, user.getUserName());
			stmt.executeUpdate();
		}
	}
	
	public UserInDB getUserFromDatabase(String name) throws SQLException {
		PreparedStatement stmt = null;

		stmt = conn.prepareStatement(GET_USER_BY_NAME_STATEMENT);
		stmt.setString(1, name);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		
		return new UserInDB(
				rs.getInt(1),
				rs.getString(2),
				rs.getInt(3),
				rs.getInt(4)
				);
	}
	
	public UserInDB[] getTopUsers(int amount) throws SQLException {
		PreparedStatement stmt = null;
		UserInDB[] users = new UserInDB[amount];
		
		stmt = conn.prepareStatement(GET_TOP_USERS_STATEMENT);
		stmt.setInt(1, amount);
		ResultSet rs = stmt.executeQuery();
		
		int i = 0;
		while(rs.next()) {
			users[i] = new UserInDB(
					rs.getInt(1),
					rs.getString(2),
					rs.getInt(3),
					rs.getInt(4)
					);
			i++;
		}

		return users;
	}
	
	public UserInDB[] getTopUsersByTime(int amount) throws SQLException {
		PreparedStatement stmt = null;
		UserInDB[] users = new UserInDB[amount];
		
		stmt = conn.prepareStatement(GET_TOP_USERS_BY_TIME_STATEMENT);
		stmt.setInt(1, amount);
		ResultSet rs = stmt.executeQuery();
		
		int i = 0;
		while(rs.next()) {
			users[i] = new UserInDB(
					rs.getInt(1),
					rs.getString(2),
					rs.getInt(3),
					rs.getInt(4)
					);
			i++;
		}

		return users;
	}
	
	public UserInDB getUserFromDatabase(int id) throws SQLException {
		PreparedStatement stmt = null;	
	
		stmt = conn.prepareStatement(GET_USER_BY_ID_STATEMENT);
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		
		return new UserInDB(
				rs.getInt(1),
				rs.getString(2),
				rs.getInt(3),
				rs.getInt(4)
				);

	}
	
	public SimpleTwitchUser[] getUsersInChannel() throws IOException {
		JSONParser jp = new JSONParser();
			
		/////// Get usernames in channel
			
		URL url = new URL(USER_REQUEST_URL);
		HttpURLConnection request = (HttpURLConnection) url.openConnection();
		request.setRequestMethod("GET");
		request.setRequestProperty("Accept", "application/vnd.twitchtv.v5+json");
		request.setRequestProperty("Client-ID", BotDriver.props.getProperty("twitch-api-client-id"));
		request.connect();
		
		///////// Parse JSON data into different user types
		
		JSONObject obj;
		try {
			obj = (JSONObject) jp.parse(new InputStreamReader((InputStream) request.getContent()));
			JSONArray staff = (JSONArray) ((JSONObject)obj.get("chatters")).get("staff");
			JSONArray viewers = (JSONArray) ((JSONObject)obj.get("chatters")).get("viewers");
			JSONArray globalMods = (JSONArray) ((JSONObject)obj.get("chatters")).get("global_mods");
			JSONArray mods = (JSONArray) ((JSONObject)obj.get("chatters")).get("moderators");
			JSONArray admins = (JSONArray) ((JSONObject)obj.get("chatters")).get("admins");
			
			///////// Add every user type to one big list of usernames
			
			List<String> userNamesInChannel = new ArrayList<String>();
			
			userNamesInChannel.addAll(0, staff);
			userNamesInChannel.addAll(0, viewers);
			userNamesInChannel.addAll(0, globalMods);
			userNamesInChannel.addAll(0, mods);
			userNamesInChannel.addAll(0, admins);	
				
			//////// API call to look up IDs of every user in chat
			if (userNamesInChannel.size() == 0) return null;
			return lookupTwitchId(userNamesInChannel).toArray(new SimpleTwitchUser[0]);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private List<SimpleTwitchUser> lookupTwitchId(List<String> userNames) {
		if(userNames.size() == 0) return null;
		
		/// Initialize array to return
		List<SimpleTwitchUser> result = new ArrayList<SimpleTwitchUser>();
		
		/// Calculate how many API calls to make
		/// Put them in batches of 100
		int numberOfApiCalls = 1 + (userNames.size() / 100);
		
		/// Set up executor service and list of pending results
		ExecutorService executor = Executors.newFixedThreadPool(50);
		List<Future<SimpleTwitchUser[]>> pendingResults = 
				new ArrayList<Future<SimpleTwitchUser[]>>();
		
		/// Tell the executor to make API calls
		while (numberOfApiCalls > 0) {
			/// Prepare one batch of up to 100 users to request
			List<String> batch = new ArrayList<String>();
			for(int i = 0; i < 100 && userNames.size() > 0; i++) {
				batch.add(userNames.remove(0));
			}
			
			/// Instantiate requester and give it one batch
			TwitchIdAPIRequester requester = new TwitchIdAPIRequester(batch);
			/// Add Future object to list of pending results
			pendingResults.add(executor.submit(requester));
			
			numberOfApiCalls--;
		}
		
		
		
		/// Loop through list of pending results to get the data
		for (Future<SimpleTwitchUser[]> pendingResult : pendingResults) {
			try {
				/// Get one batch from the future object
				/// Timeout if any single batch takes > 2 seconds
				SimpleTwitchUser[] batch = 
						(SimpleTwitchUser[]) pendingResult.get(2, TimeUnit.SECONDS);
				
				if (batch != null) {
					for(SimpleTwitchUser usr : batch) {
						result.add(usr);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		executor.shutdownNow();
		
		return result;
	}

	
	
}
