package bot.irc;

public class Channel {
	private String name;
	
	public Channel(String name) {
		this.name = name.toLowerCase();
	}
	
	@Override
	public String toString() {
		return name;
	}
}
