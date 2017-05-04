import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


//TODO Do I have to make increase/decrease pending variable for both this & work queue class?

public class ThreadSafeInvertedIndexBuilder {
	
	private static final Logger logger = LogManager.getLogger();
	private final WorkQueue workers;

	
	public ThreadSafeInvertedIndexBuilder(ThreadSafeInvertedIndex index, int numThreads) {
		super();
		workers = new WorkQueue(numThreads);
	}
	


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
	public void traverse(Path path, ThreadSafeInvertedIndex index) throws IOException {
		if (Files.isDirectory(path)) {
			
			try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
				
				for (Path extension : listing) {
					traverse(extension, index);
				}
			}
		}
		else if (path.toString().toLowerCase().endsWith("htm") || path.toString().toLowerCase().endsWith("html")) {
			workers.execute(new FileWorker(path, index));
		}
		workers.finish();
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
	public static void buildIndex(Path path, ThreadSafeInvertedIndex index) throws IOException {
		List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
		String html = String.join(" ", lines);
		String cleanedWords = HTMLCleaner.stripHTML(html);
		String[] words = WordParser.parseWords(cleanedWords);
		
		index.addAll(words, path);
	}
	
	private class FileWorker implements Runnable {
		private Path path;
		private ThreadSafeInvertedIndex index;
		
		public FileWorker(Path path, ThreadSafeInvertedIndex index) {
			this.path = path;
			this.index = index;
		}
		
		@Override
		public void run() {
			try {
				buildIndex(path, index);
			}
			catch (IOException e) {
				logger.warn("Unable to parse {}");
			}
		}
	}

}