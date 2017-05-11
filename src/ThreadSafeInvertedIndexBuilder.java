import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Builds the index and traverses through a directory.
 *
 */
public class ThreadSafeInvertedIndexBuilder {
	
	private static final Logger logger = LogManager.getLogger();
	private final WorkQueue workers;

	/**
	 * Initializes the thread safe inverted index.
	 * @param index
	 * 				thread safe index to build
	 * @param workers
	 * 				work queue object
	 */
	public ThreadSafeInvertedIndexBuilder(ThreadSafeInvertedIndex index, WorkQueue workers) {
		super();
		this.workers = workers;
		// TODO Set the ThreadSafeIndex here, and remove as a parameter elsewhere
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
		traverseHelper(path, index);
		workers.finish();
	}
	
	/**
	 * Helper method to traverse
	 * 
	 * @param path
	 * 				path to traverse
	 * @param index
	 * 				the inverted index to add words to
	 * @throws IOException
	 */
	private void traverseHelper(Path path, ThreadSafeInvertedIndex index) throws IOException {
		if (Files.isDirectory(path)) {
			
			try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
				
				for (Path extension : listing) {
					traverseHelper (extension, index);
				}
			}
		}
		else if (path.toString().toLowerCase().endsWith("htm") || path.toString().toLowerCase().endsWith("html")) {
			workers.execute(new FileWorker(path, index));
		}
	}
	
	/**
	 * Adds the path into the structure &
	 * builds the index
	 */
	private class FileWorker implements Runnable {
		Path path;
		ThreadSafeInvertedIndex index;
		
		public FileWorker(Path path, ThreadSafeInvertedIndex index) {
			this.path = path;
			this.index = index;
		}
		
		@Override
		public void run() {
			try {
				// TODO Comment this out
				InvertedIndexBuilder.buildIndex(path, index);				
				logger.debug("Building index: " + path);
				
				InvertedIndex local = new InvertedIndex();
				InvertedIndexBuilder.buildIndex(path, local);
				index.addAll(local);
			}
			catch (IOException e) {
				logger.debug("Unable to parse {}");
			}
		}
	}

}