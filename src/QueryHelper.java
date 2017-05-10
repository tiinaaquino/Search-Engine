import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

/**
 * This class is responsible for parsing, cleaning,
 * and storing queries.
 */

public class QueryHelper implements QueryInterface{
	
	/**
	 * Stores the query in a map where the key is the cleaned line.
	 */
	private final TreeMap<String, ArrayList<SearchResult>> result;
	private final InvertedIndex index;
	
	/**
	 * Initializes an empty result map.
	 */
	public QueryHelper(InvertedIndex index) {
		this.result = new TreeMap<>();
		this.index = index;
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
	public void parse(Path path, boolean exact) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			String line;
			
			while ((line = reader.readLine()) != null) {
				String[] words = WordParser.parseWords(line);
				
				if (words.length == 0) {
					continue;
				}
				
				Arrays.sort(words);
				line = String.join(" ", words);
				ArrayList<SearchResult> list = exact ? index.exactSearch(words) : index.partialSearch(words);
				result.put(line, list);
			}
		}
	}
		
	/**
	 * writes object to JSON format
	 * 
	 * @param path
	 * 			path to input
	 * @throws IOException
	 */
	public void toJSON(Path path) throws IOException {
		JSONWriter.asSearchObject(result, path);
	}
	
	@Override
	public String toString() {
		return ("Search [result=" + result + "]");
	}
}
