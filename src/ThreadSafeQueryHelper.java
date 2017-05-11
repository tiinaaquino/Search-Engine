import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is responsible for parsing, cleaning,
 * and storing queries.
 */

public class ThreadSafeQueryHelper implements QueryInterface{
	
	/**
	 * Stores the query in a map where the key is the cleaned line.
	 */
	private final TreeMap<String, ArrayList<SearchResult>> result;
	/**
	 * Thread safe inverted index
	 */
	private final ThreadSafeInvertedIndex index;
	/**
	 * Lock object for multi threading
	 */
	private final ReadWriteLock lock;
	/**
	 * Worker thread object
	 */
	private final WorkQueue workers;
	
	private static final Logger logger = LogManager.getLogger();
	
	/**
	 * Initializes an empty map result
	 */
	public ThreadSafeQueryHelper(ThreadSafeInvertedIndex index, WorkQueue workers) {
		super();
		result = new TreeMap<>();
		this.workers = workers;
		lock = new ReadWriteLock();
		this.index = index;
	}
	
	@Override
	public void parse(Path path, boolean exact) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			String line;
			
			while ((line = reader.readLine()) != null) {
				workers.execute(new LineWorker(line, index, result, exact));
			}
			workers.finish();
		}
	}
	
	@Override
	public void asJSON(Path path) throws IOException {
		lock.lockReadOnly();
		try {
			JSONWriter.asSearchObject(result, path);
		}
		finally {
			lock.unlockReadOnly();
		}
	}
	
	/**
	 * Inner class to help parse
	 */
	private class LineWorker implements Runnable {
		private String line;
		private boolean exact;
		private final ThreadSafeInvertedIndex index;
		private final TreeMap<String, ArrayList<SearchResult>> result;
		
		public LineWorker(String line, ThreadSafeInvertedIndex index, TreeMap<String, ArrayList<SearchResult>> result, boolean exact) {
			this.line = line;
			this.index = index;
			this.result = result;
			this.exact = exact;

		}
		
		@Override
		public void run() {
			String[] words = WordParser.parseWords(line);
			
			if (words.length == 0) {
				return;
			}
			
			Arrays.sort(words);
			line = String.join(" ", words);
			
			ArrayList<SearchResult> localList = exact ? index.exactSearch(words) : index.partialSearch(words);
			lock.lockReadWrite();
			
			try {
				result.put(line, localList);
			}
			finally {
				lock.unlockReadWrite();
			}
			logger.debug("Result: " + result);
		}
	}

}