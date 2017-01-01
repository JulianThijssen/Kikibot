package bot.irc;

import java.io.IOException;
import java.util.List;

import bot.util.FileUtils;
import bot.util.Log;

public class Config {
	public static LoginDetails loadUser(String userFile) {
		try {
			List<String> lines = FileUtils.loadLines(userFile);
			
			LoginDetails user = new LoginDetails(lines.get(0), lines.get(1), lines.get(2));
			return user;
		} catch (IOException e) {
			Log.error("Failed to load login file");
		} catch (IndexOutOfBoundsException e) {
			Log.error("Login file is incomplete, should contain Username, Nickname and OAuth key");
		}
		return null;
	}
}
