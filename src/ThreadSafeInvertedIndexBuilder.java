import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ThreadSafeInvertedIndexBuilder {
	
	private static final Logger logger = LogManager.getLogger();
	private final WorkQueue workers;  // TODO store the number of threads

	
	public ThreadSafeInvertedIndexBuilder(ThreadSafeInvertedIndex index, int numThreads) {
		super();
		workers = new WorkQueue(numThreads);
	}
	
	/* TODO
	public void traverse(Path path) {
		WorkQueue queue = new WorkQueue(threads);
		
		traverseHelper(path, queue);
		queue.finish();
		queue.shutdown();
	}
	*/

	// TODO Make a private traverseHelper(Path path, WorkQueue queue)
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
					traverse(extension, index); // TODO Call traverseHelper
				}
			}
		}
		else if (path.toString().toLowerCase().endsWith("htm") || path.toString().toLowerCase().endsWith("html")) {
			workers.execute(new FileWorker(path, index));
		}
		workers.finish(); // TODO Remove
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
				InvertedIndexBuilder.buildIndex(path, index);
			}
			catch (IOException e) {
				logger.warn("Unable to parse {}");
			}
		}
	}

}