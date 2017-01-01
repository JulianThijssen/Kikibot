package bot.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
	public static List<String> loadLines(String filepath) throws IOException {
		List<String> lines = new ArrayList<String>();
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(filepath));
		
			String line = null;
			while ((line = in.readLine()) != null) {
				lines.add(line);
			}
			
			in.close();
		} catch (FileNotFoundException e) {
			System.out.println("Could not find file: " + filepath);
		}
		
		return lines;
	}
}
