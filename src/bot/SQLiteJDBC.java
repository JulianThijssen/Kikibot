package bot;

import java.util.List;

import bot.classification.User;
import bot.database.UserDatabase;

public class SQLiteJDBC {
	public static void main(String args[]) {
//		Connection c = null;
//		Statement stmt = null;
//		try {
//			Class.forName("org.sqlite.JDBC");
//			c = DriverManager.getConnection("jdbc:sqlite:test.db");
//			System.out.println("Opened database successfully");
//
//			stmt = c.createStatement();
//			String sql = "CREATE TABLE COMPANY " + "(ID INT PRIMARY KEY     NOT NULL,"
//					+ " NAME           TEXT    NOT NULL, " + " AGE            INT     NOT NULL, "
//					+ " ADDRESS        CHAR(50), " + " SALARY         REAL)";
//			stmt.executeUpdate(sql);
//			stmt.close();
//			c.close();
//		} catch (Exception e) {
//			System.err.println(e.getClass().getName() + ": " + e.getMessage());
//			System.exit(0);
//		}
//		System.out.println("Table created successfully");
		UserDatabase db = new UserDatabase();
		db.open("test.db");
		db.setTable("users");
		db.addTable();

		//db.addTable();
		List<User> users = db.fetchUsers();
		db.close();
		
		for (User user: users) {
			System.out.println(user);
		}
	}
}
