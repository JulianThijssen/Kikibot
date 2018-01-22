package bot.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import bot.classification.User;
import bot.util.Log;

public class UserDatabase {
	private String table;
	
	Connection c = null;

	
	public void setTable(String table) {
		this.table = table;
	}
	
	public void open(String db) {
		try {
			Log.debug("Opening database...");
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + db);
			c.setAutoCommit(false);
			Log.debug("Opened database successfully!");
		} catch (Exception e) {
			Log.error(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public void executeStatement(String statement) {
		try {
			Statement stmt = c.createStatement();
		
			stmt.executeUpdate(statement);
			stmt.close();
			c.commit();
		} catch (SQLException e) {
			Log.error("Database: Failed to execute SQL statement: " + e.getMessage());
		}
	}
	
	public void addTable() {
		Log.debug("Creating new table...");
		String sql = "CREATE TABLE IF NOT EXISTS " + table + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
										   + " NAME           TEXT    NOT NULL UNIQUE, "
										   + " POINTS         INT     NOT NULL, "
										   + " POSTS          INT     NOT NULL)";
		
		executeStatement(sql);
		Log.debug("Table created!");
	}
	
	public void dropTable() {
		Log.debug("Dropping table...");
		String sql = "DROP TABLE IF EXISTS " + table;
		
		executeStatement(sql);
		Log.debug("Table dropped!");
	}

	public void insert(User user) {
		String sql = String.format("INSERT OR IGNORE INTO %1$s (NAME, POINTS, POSTS) VALUES ('%2$s', %3$d, %4$d); UPDATE %1$s SET POINTS=%3$d, POSTS=%4$d WHERE NAME='%2$s';", table, user.name, user.points, user.posts);
		
		executeStatement(sql);
	}
	
	public void sync() {
		String newUsers = "SELECT * FROM users_sync";
		
		try {
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(newUsers);
			while (rs.next()) {
				String name = rs.getString("name");
				int points = rs.getInt("points");
				int posts = rs.getInt("posts");
				User user = new User(name, points, posts);
				insert(user);
			}
			rs.close();
			stmt.close();
			c.commit();
		} catch (SQLException e) {
			Log.error("Database: Failed to sync: " + e.getMessage());
		}
	}

	public List<User> fetchUsers() {
		addTable();
		
		List<User> users = new ArrayList<User>();
		
		try {
			Statement stmt = c.createStatement();
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + table + ";");
			
			while (rs.next()) {
				String name = rs.getString("name");
				int points = rs.getInt("points");
				int posts = rs.getInt("posts");
				User user = new User(name, points, posts);
				users.add(user);
			}
		} catch (SQLException e) {
			Log.error("Database: Failed to fetch user: " + e.getMessage());
		}
		return users;
	}

	public void close() {
		try {
			Log.debug("Closing database...");
			c.close();
			Log.debug("Database closed!");
		} catch (SQLException e) {
			Log.error("Database: Failed to disconnect.");
		}
	}
}
