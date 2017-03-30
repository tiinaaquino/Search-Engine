import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;

/**
 * Builds the inverted index data structure.
 */
public class InvertedIndexBuilder {
	
	/**
	 * Traverses the directory, if the file ends in "HTML" buildIndex
	 * method is called
	 * 
	 * @param path
	 * 				path to traverse
	 * @param index 
	 * 				the inverted index to add words to
	 * @throws IOException
	 */
	public static void traverse(Path path, InvertedIndex index) throws IOException {
			if (Files.isDirectory(path)) {
				try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
					for (Path extension : listing) {
						traverse(extension, index);
					}
				}
			}
			else if (path.toString().toLowerCase().endsWith(".html") || path.toString().toLowerCase().endsWith(".htm")) {
				buildIndex(path, index);
			}
	}
	
	/**
	 * Reads through the file, cleans HTML tags, parses the words then
	 * adds them to the index.
	 * 
	 * @param path
	 * 				path to add to the index
	 * @param index
	 * 				data structure to add in the words
	 * 				from the path
	 * @throws IOException
	 */
	public static void buildIndex(Path path, InvertedIndex index) throws IOException {
		List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
		String html = String.join(" ", lines);
		String cleanedWords = HTMLCleaner.stripHTML(html);
		String[] words = WordParser.parseWords(cleanedWords);
		
		index.addAll(words, path);
	}
}