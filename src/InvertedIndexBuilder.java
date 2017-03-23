import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;

public class InvertedIndexBuilder {
	
	
	/**
	 * Directory to traverse
	 * @param path
	 * @param index
	 * @throws IOException
	 */
	
	public static void traverseDirectory(Path path, InvertedIndex index) throws IOException  // TODO Remove
	{
		traverse(path, index);
	}
	
	
	/**
	 * Traverses the directory, if the file ends in "HTML" buildIndex
	 * method is called
	 * @param path
	 * @param index
	 * @throws IOException
	 */
	public static void traverse(Path path, InvertedIndex index) throws IOException {
		if (Files.isDirectory(path)) {
			try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
				
				for (Path extension : listing) {
					
					if (Files.isDirectory(extension)) {
						traverseDirectory(extension, index);
					}
					
					if (extension.toString().toLowerCase().endsWith(".html") || extension.toString().toLowerCase().endsWith(".htm")) {
						buildIndex(extension, index);
					}
				}
			}
			catch (Exception e) { // TODO Remove the catch
				e.printStackTrace();
			}
		} // TODO else if...
		if (path.toString().toLowerCase().endsWith("html") || path.toString().toLowerCase().endsWith("htm")) {
			buildIndex(path, index);
		}
	}
	
	/* TODO
	public static void traverse(Path path, InvertedIndex index) throws IOException {
		if (Files.isDirectory(path)) {
			try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
				
				for (Path extension : listing) {
					traverse(extension, index);
				}
			}
		}
		else if (path.toString().toLowerCase().endsWith("html") || path.toString().toLowerCase().endsWith("htm")) {
			buildIndex(path, index);
		}
		
	}
	*/
	
	/**
	 * Reads through the file, cleans HTML tags, parses the words then
	 * adds them to the index.
	 * 
	 * @param path
	 * @param index
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