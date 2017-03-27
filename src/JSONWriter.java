import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
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
	 *
	 * @param writer
	 *            writer to use for output
	 * @param elements
	 *            elements to write as JSON array
	 * @param level
	 *            number of times to indent the array itself
	 * @throws IOException
	 */
	private static void asArray(Writer writer, TreeSet<Integer> elements, int level) throws IOException {				
		writer.write("[");
		writer.write(System.lineSeparator());
		
		if (elements.isEmpty()) {
			writer.write(indent(level + 1));
		}
		else {
			writer.write(indent(level + 1) + elements.first().toString());
			for (Integer elem: elements.tailSet(elements.first(), false)) {
				writer.write(",");
				writer.write(System.lineSeparator());
				writer.write(indent(level + 1) + elem.toString());
			}
			writer.write(System.lineSeparator());
			writer.write(indent(level) + "]");
		}
	}

	/**
	 * Writes the set of elements as a JSON array to the path using UTF8.
	 *
	 * @param elements
	 *            elements to write as a JSON array
	 * @param path
	 *            path to write file
	 * @throws IOException
	 */
	public static void asArray(TreeSet<Integer> elements, Path path) throws IOException {		
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asArray(writer, elements, 0);
		}
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
			if (elements.isEmpty()){
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
	public static void asNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);) {
			writer.write("{");
			
			int i = 0;
			int j = 0;
			int pathCount = 0;
			int positionCount = 0;
			
			for (String k : elements.keySet()) {
				writer.write("\n" + indent(1) + quote(k) + ": {\n");
				int l = 0;
				
				for (String m : elements.get(k).keySet()) {
					i = 0;
					int numCommas = 0;
					pathCount = elements.get(k).size();
					writer.write(indent(2) + quote(String.valueOf(m)) + ": [");
					
					for (int n : elements.get(k).get(m)) {
						positionCount = elements.get(k).get(m).size();
						writer.write("\n"+indent(3) + n);
						
						while (i < positionCount-1) {
							i++;
							writer.write(",");
							break;
						}
						
						if (positionCount-1 >= numCommas) {
							if (positionCount-1 == numCommas) {
								writer.write("\n"+indent(2) + "]");
								
								while (l < pathCount-1) {
									l++;
									writer.write(",");
									break;
								}
							}
							numCommas++;
						}
					}
					writer.write("\n");
				}
				writer.write(indent(1) + "}");
				
				while (j < elements.size()-1) {
					writer.write(",");
					j++;
					break;
				}
			}
			writer.write("\n}");
		}
	}
}
