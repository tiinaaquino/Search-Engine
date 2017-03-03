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
		Iterator<Integer> iterator = elements.iterator();
		
		writer.write("[");
		writer.write(System.lineSeparator());
		
		while(iterator.hasNext()) {
			writer.write(indent(level + 1));
			writer.write(iterator.next().toString());
			
			if (iterator.hasNext()) {
				writer.write(",");
			}
			writer.write(System.lineSeparator());
		}
		
		writer.write("]");
		writer.write(System.lineSeparator());
		writer.flush();
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
		// TODO Use try-with-resources (no catch block needed)
		
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
	 * Writes the set of elements as a JSON object with a nested array to the
	 * path using UTF8.
	 *
	 * @param elements
	 *            elements to write as a JSON object with a nested array
	 * @param path
	 *            path to write file
	 * @throws IOException
	 */
	public static void asNestedObject(TreeMap<String, TreeSet<Integer>> elements, Path path) throws IOException {
		
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			writer.write("{");
			if (elements.isEmpty()){
				writer.write(System.lineSeparator());
				writer.write("}");
			}
			else {
				writer.write(System.lineSeparator());
				String last = elements.lastKey();
//				Map.Entry<String, TreeSet<Integer>> entry = elements.lastEntry();
								
				for (Map.Entry<String, TreeSet<Integer>> entry: elements.entrySet()) {					
					writer.write(indent(1));
					writer.write("\"" + entry.getKey()+ "\": ");
					asArray(writer, entry.getValue(), 0);

//					if (entry != last) {
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
}
