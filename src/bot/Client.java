package bot;

import java.util.Calendar;

import bot.classification.Classifier;
import bot.classification.NameBase;
import bot.classification.SimpleClassifier;
import bot.classification.User;
import bot.irc.Channel;
import bot.irc.LoginDetails;
import bot.network.IRCConnection;
import bot.util.Log;

public class Client implements IRCListener {
	/** The IRC connection */
	private IRCConnection connection;
	
	/** Send a names query every 5 seconds */
	private QueryThread queryThread;
	
	private NameBase database = new NameBase();
	
	private String chatHistory = "";
	private int count = 0;
	
	public Client(String host, int port, LoginDetails loginDetails) {
		connection = new IRCConnection(this, host, port, loginDetails);
		queryThread = new QueryThread(connection);
	}
	
	public void connect(Channel channel) {
		connection.connect(channel);
		
		database.load();
		
		queryThread.start();
	}

	@Override
	public void onMessage(String user, String message) {
		System.out.println("[" + user + "]: " + message);
		chatHistory += "[" + user + "]: " + message + "\n";
		
		User dbuser = database.getUser(user);
		if (dbuser == null) {
			return;
		}
		
		Classifier classifier = new SimpleClassifier();
		if (classifier.classify(dbuser)) {
			Log.debug("I WOULD NOW SAY SOMETHING");
			
			int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
			if (hour < 8) {
				int when = 8 - hour;
				if (when >= 2) {
					connection.sendChat("Hi " + user + ". Welcome! Unfortunately most of the devs are asleep at this time. If you wanted to ask something please ask again in " + when + " hours.");
				} else if (when == 1) {
					connection.sendChat("Hi " + user + ". Welcome! Unfortunately most of the devs are asleep at this time. If you wanted to ask something please ask again in 1 hour.");
				} else if (when == 0) {
					connection.sendChat("Hi " + user + ". Welcome! Unfortunately most of the devs are asleep at this time. If you wanted to ask something please ask again in a couple of minutes");
				}
			} else {
				connection.sendChat("Hello " + user + "! It seems you are new here, if you asked a question please stay in the chatroom for a while. It might take a few minutes for people to read it. Thank you!");
			}
		}
		database.addPost(user);
	}

	@Override
	public void onNames(String[] names) {
		for (String name: names) {
			database.addName(name);
			database.addPoints(name, 1);
		}
		
		count++;
		if (count > 5) {
			database.save();
			count = 0;
		}
	}

	@Override
	public void onJoin(String user) {
		
	}

	@Override
	public void onPart() {
		
	}
	
	@Override
	public void onPing() {
		connection.sendPong();
	}
}
