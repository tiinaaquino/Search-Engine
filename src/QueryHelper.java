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

public class QueryHelper {
	
	private final HashMap<String, ArrayList<SearchResult>> result;
	
	public QueryHelper() {
		result = new LinkedHashMap<>();
	}
	
	public void print(Path output) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(output, Charset.forName("UTF-8"))) {
			writer.write("{");
			if (!result.isEmpty()) {
				Iterator<String> keys = result.keySet().iterator();
				
				if (keys.hasNext()) {
					String key = keys.next();
					JSONWriter.asNestedMap(key, result.get(key), writer);
				}
				
				while (keys.hasNext()) {
					String key = keys.next();
					writer.write(",");
					JSONWriter.asNestedMap(key, result.get(key), writer);
				}
			}
			writer.newLine();
			writer.write("}");
		}
	}
	
//	public void parseFile (Path file, InvertedIndex index) throws IOException {
//		try (BufferedReader reader = Files.newBufferedReader(file, Charset.forName("UTF-8"))) {
//			String line = null;
//			while ((line = reader.readLine()) != null) {
//				parseLinePartially(line, index);
//			}
//		}
//	}
	
//	public void parseLinePartially(String line, InvertedIndex index) {
//		String[] queryWords = WordParser.split(line);
//		String[] queryWords = WordParser.clean(line).replaceAll("\\s{2,}", " ").split(line);
//		ArrayList<SearchResult> resultList = index.partialSearch(queryWords);
//		result.put(line, resultList);
//	}
	
	public static ArrayList<String> parse(Path path) throws IOException {
		ArrayList<String> list = new ArrayList<String>();
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
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
