package bot;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;

import bot.irc.Channel;
import bot.irc.Config;
import bot.irc.LoginDetails;

public class DaemonBot implements Daemon {
	private static final String SERVICE = "irc.freenode.net";
	private static final int PORT = 6667;
	private static final Channel KRITA_CHANNEL = new Channel("#krita");
	
	@Override
	public void init(DaemonContext context) throws DaemonInitException, Exception {

	}

	@Override
	public void start() throws Exception {
		LoginDetails user = Config.loadUser("userKikibot");
		
		// Start up the IRC client
		Client client = new Client(SERVICE, PORT, user);
		
		// Connect to the Krita channel
		client.connect(KRITA_CHANNEL);
	}

	@Override
	public void stop() throws Exception {
		
	}

	@Override
	public void destroy() {
		
	}
}
