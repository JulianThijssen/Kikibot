package bot;

public interface IRCListener {
	public void onMessage(String user, String message);
	public void onNames(String[] names);
	public void onJoin(String user);
	public void onPart();
	public void onPing();
}
