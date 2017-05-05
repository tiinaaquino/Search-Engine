import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

// TODO Use @Override, but do not need to Javadoc overridden methods

public class ThreadSafeInvertedIndex extends InvertedIndex{
	
	
	//TODO comments
	private final ReadWriteLock lock;

	
	public ThreadSafeInvertedIndex() {
		super();
		lock = new ReadWriteLock();
	}
	
	// TODO Remove the synchronized, but still need to use the readwrite lock
	public synchronized void add(String word, String path, int wordPosition) {
			super.add(word, path, wordPosition);
	}
	

	public void addAll(String[] words, Path path) {
		lock.lockReadWrite();
		
		try {
			// TODO super.addAll(...);
			int position = 1;
			for (String i : words) {
				super.add(i, path.toString(), position);
				position++;
			}
		}
		finally {
			lock.unlockReadWrite();
		}
	}
	

	public ArrayList<SearchResult> partialSearch(String[] queryWords) {
		lock.lockReadOnly();
		
		try {
			return super.partialSearch(queryWords);
		}
		finally {
			lock.unlockReadOnly();
		}
	}
	

	public ArrayList<SearchResult> exactSearch(String[] queryWords) {
		lock.lockReadOnly();
		
		try {
			return super.exactSearch(queryWords);
		}
		finally {
			lock.unlockReadOnly();
		}
	}
	
	/**
	 * Calls "asNestedOject" of the JSONWriter class to convert object 
	 * to JSON object.
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void asJSON(Path path) throws IOException {
		lock.lockReadWrite(); // TODO Lock for read
		
		try {
			super.asJSON(path);
		}
		finally {
			lock.unlockReadWrite();
		}
	}
	
	// TODO Do have to override and lock numWords, etc. contains(), toString, copy methods.
	
}
