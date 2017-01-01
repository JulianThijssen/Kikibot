package bot.database;

public interface Database {
	public void open(String db);
	public void addTable();
	public void insert();
	public void update();
	public void fetch();
	public void delete();
	public void close();
}
