import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;


/**
 * Stores object to JSON format
 */
public class JSONWriter {

	/**
	 * Returns a String with the specified number of tab characters.
	 *
	 * @param times
	 *            number of tab characters to include
	 * @return tab characters repeated the specified number of times
	 */
	public static String indent(int times) {
		char[] tabs = new char[times];
		Arrays.fill(tabs, '\t');
		return String.valueOf(tabs);
	}

	/**
	 * Returns a quoted version of the provided text.
	 *
	 * @param text
	 *            text to surround in quotes
	 * @return text surrounded by quotes
	 */
	public static String quote(String text) {
		return String.format("\"%s\"", text);
	}
	
	/**
	 * Writes the set of elements as a JSON array at the specified indent level.
	 * @param writer
	 * 				writer to use for output
	 * @param elements
	 * 				elements to write as JSON array
	 * @param level
	 * 				number of times to indent the array itself
	 * @throws IOException
	 */
	private static void asArray(Writer writer, TreeSet<Integer> elements, int level) throws IOException
	{	
		Iterator<Integer> iterator = elements.iterator();
		writer.write("[");
		
		if (iterator.hasNext()) {
			writer.write("\n");
			writer.write(indent(1));
			writer.write(iterator.next().toString());
		}

		while (iterator.hasNext()) {
			writer.write(",\n");
			writer.write(indent(1));
			writer.write(iterator.next().toString());
		}

		writer.write("\n");
		writer.write(']');
		writer.flush();
	}

	/**
	 * Writes the map of elements as a JSON object to the path using UTF8.
	 *
	 * @param elements
	 *            elements to write as a JSON object
	 * @param path
	 *            path to write file
	 * @throws IOException
	 */
	public static void asObject(TreeMap<String, Integer> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			
			writer.write("{");
			if (elements.isEmpty()) {
				writer.write(System.lineSeparator());
				writer.write("}");
			}
			else {
				writer.write(System.lineSeparator());
				String last = elements.lastKey();
			
				for (Map.Entry<String, Integer> entry: elements.entrySet()) {
					writer.write(indent(1));
					writer.write("\"" + entry.getKey()+ "\": ");
					writer.write(entry.getValue().toString());
					
					if (entry.getKey() != last) {
						writer.write(",");
					}
					writer.write(System.lineSeparator());
				}
				writer.write("}");
				writer.write(System.lineSeparator());
				writer.flush();	
			}
		}
	}
	
	/**
	 * Helper method to output the key for every key/ value pair
	 * @param elements
	 *  			key/ value pair
	 */
	public static void printKeys(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements) {
		for (Map.Entry<String, TreeMap<String, TreeSet<Integer>>> entry: elements.entrySet()) {
			System.out.println(entry.getKey());
		}
	}
	
	/**
	 * Helper method to output the value for every key/ value pair
	 * @param elements
	 * 			key/ value pair
	 */
	public static void printValues(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements) {
		for (Map.Entry<String, TreeMap<String, TreeSet<Integer>>> entry: elements.entrySet()) {
			System.out.println(entry.getValue());
		}
	}
	
	/**
	 * Writes the set of elements as a JSON object with a nested array to the
	 * path using UTF8.
	 *
	 * @param elements
	 *            elements to write as a JSON object with a nested array
	 * @param path
	 *            path to write file
	 * @throws IOException
	 */
	private static void asNestedObject(TreeMap<String, TreeSet<Integer>> elements, BufferedWriter writer, int level) throws IOException {
		int count = 0;
		for (String s : elements.keySet()) {
			writer.write(indent(level));
			writer.write(quote(s) + ": ");
			
			if (elements.isEmpty()) {
				asArray(writer, elements.get(s), 0);
			}
			asArray(writer, elements.get(s), level);
			
			while (count < elements.size() - 1) {
				count++;
				writer.write(",");
				break;
			}
			writer.write("\n");
		}
		writer.flush();
	}
	
	/**
	 * Writes the set of elements as a JSON object with a nested array to the
	 * path using UTF8.
	 *
	 * @param elements
	 *            elements to write as a JSON object with a nested array
	 * @param path
	 *            path to write file
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	public static void asDNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			int i = 0;
			writer.write("{\n");
			
			for (String word: elements.keySet()) {
				writer.write(indent(1) + quote(word) + ": {\n");
				
				for (String s : elements.get(word).keySet()) {
					JSONWriter.asNestedObject(elements.get(word), writer, 2);
					writer.write(indent(1) + "}");
					while (i < elements.size()-1) {
						i++;
						writer.write(",\n");
						break;
					}
					break;
				}
			}
			writer.write("\n}");
		}
	}
	
	/**
	 * Prints search object in JSON format.
	 * 
	 * @param elements
	 * 			elements to write as a JSON object with a nested array
	 * @param path
	 * 			path to write file
	 * @throws IOException
	 */
	public static void asSearchObject(TreeMap<String, ArrayList<SearchResult>> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			writer.write("[\n");
			int count = 0;
			int queryNum = elements.size();
			
			for (String query : elements.keySet()) {
				int i = 0;
				writer.write(indent(1) + "{\n");
				writer.write(indent(2) + quote("queries") + ": " + quote(query) + ",\n");
				writer.write(indent(2) + quote("results") + ": [\n");
				
				for (SearchResult searchResult : elements.get(query)) {
					writer.write(indent(3) + "{\n");
					writer.write(indent(4) + quote("where") + ": " + quote(searchResult.getPath()) + ",\n");
					writer.write(indent(4) + quote("count") + ": " + searchResult.getFrequency() + ",\n");
					writer.write(indent(4) + quote("index") + ": " + searchResult.getPosition() + "\n");
					writer.write(indent(3) + "}");
					int searchCount = elements.get(query).size();
					
					while (i < searchCount - 1) {
						writer.write(",");
						i++;
						break;
					}
					writer.write("\n");
				}
				writer.write(indent(2) + "]\n");
				writer.write(indent(1) + "}");
				
				while (count < queryNum - 1) {
					writer.write(",");
					count++;
					break;
				}
				writer.write("\n");
			}
			
			writer.write("]");
		}
	}
}