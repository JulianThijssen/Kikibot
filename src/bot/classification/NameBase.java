package bot.classification;

import java.util.ArrayList;
import java.util.List;

import bot.database.UserDatabase;

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
	
//	public void save() {
//		try {
//			PrintWriter out = new PrintWriter(new File("database"));
//			
//			String list = "";
//			for (User user: users) {
//				list += String.format("%s %d %d\n", user.name, user.points, user.posts);
//			}
//			
//			out.write(list);
//			out.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		Log.debug("SAVING DATABASE");
//	}
	
	public void save() {
		db.open("test.db");
		db.setTable("users_sync");
		db.dropTable();
		db.addTable();
		
		for (User user: users) {
			db.insert(user);
		}
		
		db.setTable("users");
		db.addTable();
		db.sync();
		db.close();
	}
	
	// TODO load from SQL database
//	public void load() {
//		try {
//			BufferedReader in = new BufferedReader(new FileReader(new File("database")));
//			
//			String line = null;
//			while ((line = in.readLine()) != null) {
//				String[] tokens = line.split(" ");
//				String name = tokens[0];
//				int points = Integer.parseInt(tokens[1]);
//				int posts = Integer.parseInt(tokens[2]);
//				User user = new User(name, points, posts);
//				users.add(user);
//			}
//			in.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	public void load() {
		db.open("test.db");
		db.setTable("users");
		List<User> fetchUsers = db.fetchUsers();
		users.addAll(fetchUsers);
		System.out.println("LOADING NAME BASE: " + users.size());
		db.close();
	}
}
