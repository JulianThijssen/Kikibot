package bot;

import bot.network.IRCConnection;

public class QueryThread extends Thread {
	private IRCConnection connection;
	
	public QueryThread(IRCConnection connection) {
		this.connection = connection;
	}
	
	public void run() {
		while (true) {
			connection.sendNamesQuery();
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
