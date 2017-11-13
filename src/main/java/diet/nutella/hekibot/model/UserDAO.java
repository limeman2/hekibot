package main.java.diet.nutella.hekibot.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
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
			"SELECT id, name, coins, time " +
					"FROM users " +
					"WHERE RIGHT(name, 3) != 'bot' " +
					"ORDER BY coins DESC LIMIT ?";
	
	private static final String GET_TOP_USERS_BY_TIME_STATEMENT =
			"SELECT id, name, coins, time " +
					"FROM users " +
					"WHERE RIGHT(name, 3) != 'bot' " +
					"ORDER BY time DESC LIMIT ?";
	
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
		PreparedStatement stmt;
		
		stmt = conn.prepareStatement(UPDATE_STATEMENT);
		for(SimpleTwitchUser user : users) {
			stmt.setInt(1, user.getId());
			stmt.setString(2, user.getUserName());
			stmt.setString(3, user.getUserName());
			stmt.executeUpdate();
		}
	}
	
	public UserInDB getUserFromDatabase(String name) throws SQLException {
		PreparedStatement stmt;

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
		PreparedStatement stmt;
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
		PreparedStatement stmt;
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
		PreparedStatement stmt;
	
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
}
