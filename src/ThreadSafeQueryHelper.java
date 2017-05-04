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

public class ThreadSafeQueryHelper {
	
	/**
	 * Stores the query in a map where the key is the cleaned line.
	 */
	private final TreeMap<String, ArrayList<SearchResult>> result;
	private final ThreadSafeInvertedIndex index;
	private ReadWriteLock lock;
	private final WorkQueue workers;
	
	public ThreadSafeQueryHelper(ThreadSafeInvertedIndex index, int threads) {
		super();
		result = new TreeMap<>();
		workers = new WorkQueue(threads);
		lock = new ReadWriteLock();
		this.index = index;
	}
	
	public synchronized void parse(Path path, boolean exact) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			String line;
			
			while ((line = reader.readLine()) != null) {
				String[] words = WordParser.parseWords(line);
				
				if (words.length == 0) {
					continue;
				}
				
				Arrays.sort(words);
				line = String.join(" ", words);

				workers.execute(new LineWorker(line, index, result,  words, exact));
			}
			workers.finish();
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
		lock.unlockReadWrite();
		try {
			JSONWriter.asSearchObject(result, path);
		}
		finally {
			lock.unlockReadWrite();
		}
	}
	
	
	private class LineWorker implements Runnable {
		private String line;
		private String[] queryWords;
		private boolean exact;
		private ThreadSafeInvertedIndex index;
		private TreeMap<String, ArrayList<SearchResult>> result;
		
		public LineWorker(String line, ThreadSafeInvertedIndex index, TreeMap<String, ArrayList<SearchResult>> result, String[] queryWords, boolean exact) {
			this.line = line;
			this.result = result;
			this.index = index;
			this.queryWords = queryWords;
		}
		
		@Override
		public void run() {
			ArrayList<SearchResult> localList = exact ? index.exactSearch(queryWords) : index.partialSearch(queryWords);
			lock.lockReadWrite();
			
			try {
				result.put(line, localList);
			}
			finally {
				lock.unlockReadWrite();
			}
		}
	}

}