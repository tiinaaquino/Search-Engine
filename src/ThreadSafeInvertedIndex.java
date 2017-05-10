import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

public class ThreadSafeInvertedIndex extends InvertedIndex{
	
	/**
	 * Lock object
	 */
	private final ReadWriteLock lock;

	/**
	 * Initializes the index with a lock object
	 */
	public ThreadSafeInvertedIndex() {
		super();
		lock = new ReadWriteLock();
	}
	
	@Override
	public void add(String word, String path, int wordPosition) {
		lock.lockReadWrite();
		
		try {
			super.add(word, path, wordPosition);
		}
		finally {
			lock.unlockReadWrite();
		}
	}
	
	@Override
	public void addAll(String[] words, Path path) {
		lock.lockReadWrite();
		
		try {
			super.addAll(words, path);
		}
		finally {
			lock.unlockReadWrite();
		}
	}
	
	@Override
	public ArrayList<SearchResult> partialSearch(String[] queryWords) {
		lock.lockReadOnly();
		
		try {
			return super.partialSearch(queryWords);
		}
		finally {
			lock.unlockReadOnly();
		}
	}
	
	@Override
	public ArrayList<SearchResult> exactSearch(String[] queryWords) {
		lock.lockReadOnly();
		
		try {
			return super.exactSearch(queryWords);
		}
		finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public void asJSON(Path path) throws IOException {
		lock.lockReadOnly();
		
		try {
			super.asJSON(path);
		}
		finally {
			lock.unlockReadOnly();
		}
	}
	
	// TODO Override all the reads (toString, copy, contains, num, etc.)
}