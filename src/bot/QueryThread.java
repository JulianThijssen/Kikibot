package bot;

public class QueryThread extends Thread {
	private Client client;
	
	public QueryThread(Client client) {
		this.client = client;
	}
	
	public void run() {
		while (true) {
			client.sendNamesQuery();
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
