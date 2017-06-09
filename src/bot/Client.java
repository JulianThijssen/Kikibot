package bot;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.TimeZone;

import bot.classification.Classifier;
import bot.classification.NameBase;
import bot.classification.SimpleClassifier;
import bot.classification.User;
import bot.irc.Channel;
import bot.irc.LoginDetails;
import bot.network.IrcConnection;
import bot.util.Log;

public class Client implements IRCListener {
	/** The time zone Kikibot is based in */
	private static final ZoneId TIME_ZONE = ZoneId.of("Europe/Amsterdam");

	/** After how many queries we update the main database */
	private static final int SYNC_RATE = 10;
	
	/** The IRC connection */
	private IrcConnection connection;
	
	/** Send a names query every 5 seconds */
	private QueryThread queryThread;
	
	private NameBase database = new NameBase();
	
	private int count = 0;
	
	public Client(String host, int port, LoginDetails loginDetails) {
		connection = new IrcConnection(this, host, port, loginDetails);
		queryThread = new QueryThread(connection);
	}
	
	public void connect(Channel channel) {
		connection.connect(channel);
		
		database.load();
		
		queryThread.start();
	}

	@Override
	public void onMessage(String user, String message) {
		Log.info("[" + user + "]: " + message);
		//chatHistory += "[" + user + "]: " + message + "\n";
		
		User dbuser = database.getUser(user);
		if (dbuser == null) {
			Log.debug("Unrecognised user sent a message: " + user);
			return;
		}
		
		if (message.contains("Kikibot")) {
			if (message.contains("alive")) {
				connection.sendChat("Yes, I am still alive.");
				return;
			}
			if (message.contains("analysis")) {
				connection.sendChat("Entering analysis mode...");
				Log.info("Entering analysis mode.");
				Log.DEBUG = true;
			}
			if (message.contains("that's all")) {
				connection.sendChat("Understood...");
				Log.info("Leaving analysis mode.");
				Log.DEBUG = false;
			}
			if (message.contains("what time")) {
				connection.sendChat("It is currently " + ZonedDateTime.now(TIME_ZONE).format(DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss Z")));
				return;
			}
		}

		Classifier classifier = new SimpleClassifier();
		if (classifier.classify(dbuser)) {
			Log.info("Sending welcome message to user: " + user);
			
			int hour = ZonedDateTime.now(TIME_ZONE).getHour();
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
		if (count > SYNC_RATE) {
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
