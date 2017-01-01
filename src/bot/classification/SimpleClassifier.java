package bot.classification;

public class SimpleClassifier implements Classifier {
	@Override
	public boolean classify(User user) {
		if (user.posts == 0 && user.points < 300) {
			return true;
		}
		
		return false;
	}
}
