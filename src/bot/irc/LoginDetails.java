package bot.irc;

public class LoginDetails {
	public String user;
	public String nick;
	public String oauth;
	
	public LoginDetails(String user, String nick, String oauth) {
		this.user = user;
		this.nick = nick;
		this.oauth = oauth;
	}
}
