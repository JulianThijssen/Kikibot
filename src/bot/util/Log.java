package bot.util;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class Log {
	public static boolean DEBUG = false;
	
	private static PrintStream out = System.out;
	private static PrintStream err = System.err;
	
	public static void info(String info) {
		out.println(info);
	}
	
	public static void debug(String debug) {
		if (DEBUG) {
			out.println(debug);
		}
	}
	
	public static void error(String error) {
		err.println(error);
		System.exit(1);
	}
	
	public static void setOutput(PrintStream output) {
		out = output;
	}
	
	public static void setOutput(File file) throws FileNotFoundException {
		out = new PrintStream(file);
	}
}
