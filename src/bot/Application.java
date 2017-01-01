package bot;

import bot.Client;
import bot.irc.Channel;
import bot.irc.Config;
import bot.irc.LoginDetails;

public class Application {
	private static final String SERVICE = "irc.freenode.net";
	private static final int PORT = 6667;
	private static final Channel KRITA_CHANNEL = new Channel("#krita");
	
	public static void main(String[] args) {
		LoginDetails user = Config.loadUser("userKikibot");
		
		// Start up the IRC client
		Client client = new Client(SERVICE, PORT, user);
		
		// Connect to the Krita channel
		client.connect(KRITA_CHANNEL);
	}
}
