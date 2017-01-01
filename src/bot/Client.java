package bot;

import java.util.Calendar;

import bot.classification.Classifier;
import bot.classification.NameBase;
import bot.classification.SimpleClassifier;
import bot.classification.User;
import bot.irc.Channel;
import bot.irc.LoginDetails;
import bot.network.TcpConnection;
import bot.util.Log;

public class Client implements Listener, IRCListener {
	/** The login details used for identification to the IRC service */
	private LoginDetails loginDetails;
	/** The IRC service host we are connected to */
	private String host;
	/** The IRC service port we are connected to */
	private int port;

	/** The TCP connection to the IRC service */
	private TcpConnection connection = new TcpConnection();
	
	/** The channel we are connected to */
	public Channel channel;
	
	private long lastMessage = System.currentTimeMillis();
	private String chatHistory = "";
	
	/** Send a names query every 5 seconds */
	private QueryThread queryThread = new QueryThread(this);
	
	/** Read from the TCP connection every 100 milliseconds */
	private ExecutorThread reader = new ExecutorThread(100, connection);
	
	private NameBase database = new NameBase();
	private int count = 0;
	
	public Client(String host, int port, LoginDetails loginDetails) {
		this.host = host;
		this.port = port;
		this.loginDetails = loginDetails;
	}
	
	public void connect(Channel channel) {
		this.channel = channel;
		
		boolean connected = connection.connect(host, port);
		
		if (!connected) {
			Log.error("Failed to connect to channel: " + channel);
		}
		
		identify();
		join(channel);
		
		connection.addListener(this);
		database.load();
		reader.start();
	}

	public void identify() {
		connection.send("PASS oauth:" + loginDetails.oauth);
		connection.send("NICK " + loginDetails.nick);
		connection.send("USER " + loginDetails.user + " 8 * : " + loginDetails.user);
	}
	
	public void join(Channel channel) {
		connection.send("JOIN " + channel);
		
		queryThread.start();
	}
	
	public void sendChat(String message) {
		while(System.currentTimeMillis() < lastMessage + 2000) {
			
		}
		connection.send(String.format("PRIVMSG %s :%s", channel, message));
		lastMessage = System.currentTimeMillis();
	}
	
	public void sendNamesQuery() {
		connection.send("NAMES " + channel);
	}
	
	@Override
	public void onReceive(String received) {
		if (received.contains("PRIVMSG")) {
			int firstColon = received.indexOf(':');
			int exclamation = received.indexOf('!');
			int secondColon = received.indexOf(':', 2);
			if(firstColon == -1 || secondColon == -1 || exclamation == -1) {
				return;
			}
			
			String user = received.substring(received.indexOf(':') + 1, received.indexOf('!'));
			String message = received.substring(received.indexOf(':', 30) + 1);
			
			onMessage(user, message);
		} else if (received.contains("353")) {			
			int secondColon = received.indexOf(':', 2);
			if(secondColon == -1) {
				return;
			}
			String names = received.substring(secondColon + 1);
			String[] tokens = names.split(" ");
			
			onNames(tokens);
		} else if (received.contains("JOIN")) {
			int firstColon = received.indexOf(':');
			int exclamation = received.indexOf('!');
			String user = received.substring(firstColon + 1, exclamation);
			//client.sendChat("Good day " + user + "!");
			onJoin(user);
		} else if (received.contains("PING")) {
			onPing();
		} else {
			System.out.println(received);
		}
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
					sendChat("Hi " + user + ". Welcome! Unfortunately most of the devs are asleep at this time. If you wanted to ask something please ask again in " + when + " hours.");
				} else if (when == 1) {
					sendChat("Hi " + user + ". Welcome! Unfortunately most of the devs are asleep at this time. If you wanted to ask something please ask again in 1 hour.");
				} else if (when == 0) {
					sendChat("Hi " + user + ". Welcome! Unfortunately most of the devs are asleep at this time. If you wanted to ask something please ask again in a couple of minutes");
				}
			} else {
				//sendChat("Hello " + user + "! It seems you are new here, if you asked a question please stay in the chatroom for a while. It might take a few minutes for people to read it. Thank you!");
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
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onPing() {
		connection.send("PONG");
	}
}
