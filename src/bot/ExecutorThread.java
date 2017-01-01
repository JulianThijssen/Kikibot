package bot;

import bot.util.Log;

public class ExecutorThread extends Thread {
	private int interval;	
	private Executor executor;
	
	private boolean running = false;
	
	public ExecutorThread(int interval, Executor executor) {
		this.interval = interval;
		this.executor = executor;
	}
	
	public void start() {
		running = true;
		super.start();
	}
	
	public void end() {
		running = false;
	}
	
	@Override
	public void run() {
		while (running) {
			executor.execute();
			
			try {
				sleep(interval);
			} catch(InterruptedException e) {
				Log.error("Executor thread stopped working");
			}
		}
	}
}
