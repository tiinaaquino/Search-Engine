import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * This class is responsible for parsing, cleaning,
 * and storing queries.
 */

public class QueryHelper {
	
	/**
	 * Stores the query in a map where the key is the path.
	 */
	private final HashMap<String, ArrayList<SearchResult>> result;
	
	/**
	 * Initializes an empty result map.
	 */
	public QueryHelper() {
		result = new LinkedHashMap<>();
	}
	
	/**
	 * Parses the query file, cleans the texts, sorts each line,
	 * then stores it in a list.
	 * 
	 * @param path
	 * 				path of query
	 * @return sorted list of queries
	 * @throws IOException
	 */
	public static ArrayList<String> parse(Path path) throws IOException {
		ArrayList<String> list = new ArrayList<String>();
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			String line;
			while ((line = reader.readLine()) != null) {
				line = WordParser.clean(line).replaceAll("\\s{2,}", " ").trim();
				if (line.contains(" ")) {
					String[] words = WordParser.parseWords(line);
					Arrays.sort(words);
					line = Arrays.toString(words);
					line = WordParser.clean(line).replaceAll("\\s{2,}", " ").trim();
				}
				list.add(line);
			}
		}
		return list;
	}
	
	@Override
	public String toString() {
		return ("Search [result=" + result + "]");
	}
}
