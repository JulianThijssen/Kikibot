package bot.classification;

import java.util.ArrayList;
import java.util.List;

import bot.database.UserDatabase;
import bot.util.Log;

public class NameBase {
	private UserDatabase db = new UserDatabase();
	public List<User> users = new ArrayList<User>();
	
	public void addName(String name) {
		for (User user: users) {
			if (compare(user.name, name)) {
				user.points++;
				return;
			}
		}
		users.add(new User(name, 0, 0));
	}
	
	public void addPoints(String name, int points) {
		for (User user: users) {
			if (compare(user.name, name)) {
				user.points += points;
				return;
			}
		}
	}
	
	public void addPost(String name) {
		for (User user: users) {
			if (compare(user.name, name)) {
				user.posts++;
				return;
			}
		}
	}
	
	public User getUser(String name) {
		for (User user: users) {
			if (compare(user.name, name)) {
				return user;
			}
		}
		return null;
	}
	
	private boolean compare(String name1, String name2) {
		String n1 = name1.replaceAll("_", "");
		String n2 = name2.replaceAll("_", "");
		return n1.equals(n2);
	}
	
	public void save() {
		Log.debug("Saving users in database..");

		db.open("test.db");
		db.setTable("users_sync");
		db.dropTable();
		db.addTable();
		db.insert(users);

		db.setTable("users");
		db.addTable();
		db.sync();
		db.close();
		Log.debug("Database saved!");
	}
	
	public void load() {
		db.open("test.db");
		db.setTable("users");
		List<User> fetchUsers = db.fetchUsers();
		users.addAll(fetchUsers);
		Log.info("Loading users from database: " + users.size());
		db.close();
	}
}
