package bot.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import bot.Executor;
import bot.Listener;
import bot.util.Log;

public class TcpConnection implements Executor {
	private Socket socket = null;
	private BufferedReader in = null;
	private PrintWriter out = null;
	
	private boolean connected = false;
	
	private List<Listener> listeners = new ArrayList<Listener>();
	
	public boolean connect(String host, int port) {
		try {
			socket = new Socket(host, port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			return false;
		}
		System.out.println("Successfully connected");
		connected = true;
		return connected;
	}
	
	public void send(String message) {
		out.println(message);
	}
	
	public String read() {
		String received = null;
		
		try {
			if (!in.ready() || !connected) {
				return null;
			}
			
			received = in.readLine();
		} catch (IOException e) {
			Log.error("Failed while reading on TCP connection");
		}
		return received;
	}
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	@Override
	public void execute() {
		if (!connected) {
			return;
		}

		String received = read();
		
		if (received != null) {
			for (Listener listener: listeners) {
				listener.onReceive(received);
			}
		}
	}
}
