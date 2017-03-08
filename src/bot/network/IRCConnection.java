package bot.network;

import bot.Executor;
import bot.ExecutorThread;
import bot.IRCListener;
import bot.Listener;
import bot.irc.Channel;
import bot.irc.LoginDetails;
import bot.util.Log;

public class IRCConnection implements Listener, Executor {
	/** The login details used for identification to the IRC service */
	private LoginDetails loginDetails;
	
	/** The IRC service host we are connected to */
	private String host;
	/** The IRC service port we are connected to */
	private int port;
	
	/** The channel we are connected to */
	public Channel channel;
	
	/** The TCP connection to the IRC service */
	private TcpConnection connection = new TcpConnection();
	
	/** The IRC listener that will receive all IRC events */
	private IRCListener listener;
	
	/** Read from the TCP connection every 300 milliseconds */
	private ExecutorThread reader = new ExecutorThread(300, this);
	
	private long lastMessage = System.currentTimeMillis();
	
	private boolean reconnecting = false;
	
	public IRCConnection(IRCListener listener, String host, int port, LoginDetails loginDetails) {
		this.listener = listener;
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
		
		if (!reader.isRunning()) {
			reader.start();
		}
		reconnecting = false;
	}
	
	public void reconnect() {
		Log.info("Attempting to reconnect");
		
		reconnecting = true;
		connect(channel);
	}

	public void identify() {
		connection.send("PASS oauth:" + loginDetails.oauth);
		connection.send("NICK " + loginDetails.nick);
		connection.send("USER " + loginDetails.user + " 8 * : " + loginDetails.user);
	}
	
	public void join(Channel channel) {
		connection.send("JOIN " + channel);
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
	
	public void sendPong() {
		connection.send("PONG");
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
			
			listener.onMessage(user, message);
		} else if (received.contains("353")) {			
			int secondColon = received.indexOf(':', 2);
			if(secondColon == -1) {
				return;
			}
			String names = received.substring(secondColon + 1);
			String[] tokens = names.split(" ");
			
			listener.onNames(tokens);
		} else if (received.contains("JOIN")) {
			int firstColon = received.indexOf(':');
			int exclamation = received.indexOf('!');
			String user = received.substring(firstColon + 1, exclamation);

			listener.onJoin(user);
		} else if (received.contains("PING")) {
			listener.onPing();
		} else {
			System.out.println(received);
		}
	}
	
	@Override
	public void execute() {
		if (reconnecting) {
			return;
		}
		if (!connection.isConnected()) {
			reconnect();
			return;
		}

		String received = connection.read();

		if (received != null) {
			onReceive(received);
		}
	}
	
	public void close() {
		connection.close();
	}
}
