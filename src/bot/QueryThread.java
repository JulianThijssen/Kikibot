package bot;

import bot.network.IrcConnection;

public class QueryThread extends Thread {
	private IrcConnection connection;
	
	public QueryThread(IrcConnection connection) {
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
